package be.spiritualcenter.repository.implementation;

import be.spiritualcenter.domain.UserPrincipal;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.enums.VerificationType;
import be.spiritualcenter.exception.APIException;
import be.spiritualcenter.enums.Role;
import be.spiritualcenter.domain.User;
import be.spiritualcenter.form.UpdateForm;
import be.spiritualcenter.repository.UserRepo;
import be.spiritualcenter.repository.UserRepoJpa;
import be.spiritualcenter.rowmapper.UserRowMapper;
import be.spiritualcenter.service.EmailService;
import be.spiritualcenter.utils.SMSutil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static be.spiritualcenter.constants.Constants.DATE_FORMAT;
import static be.spiritualcenter.enums.VerificationType.ACCOUNT;
import static be.spiritualcenter.enums.VerificationType.PASSWORD;
import static be.spiritualcenter.query.UserQuery.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepoImpl implements UserRepo<User>, UserDetailsService {

    private final NamedParameterJdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;
    private final SMSutil smsUtil;
    private final UserRepoJpa userRepoJpa;

    @Override
    public User create(User user) {
        if (getEmailCount(user.getEmail().trim().toLowerCase())>0) throw new APIException("Email already exists");
        if (userRepoJpa.findAll().size()>=100) throw new APIException("Maximum 100 users reached");
        try{
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource params = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, params, holder);
            user.setId(holder.getKey().intValue());
            user.setRole(Role.USER);
            String verificationURL = getVerificationURL(UUID.randomUUID().toString(), ACCOUNT.getType());
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId",user.getId(), "url", verificationURL));
            sendEmail(user.getUsername(), user.getEmail(), verificationURL, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            return user;
        } catch (Exception e){
            log.error(e.getMessage());
            throw new APIException("An error occurred.");
        }
    }

    private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
        CompletableFuture.runAsync(() -> emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType));
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(int id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new APIException("No User found by id: " + id);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomNumeric(4);
        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, Map.of("id", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
            //sendSMSThread(user, verificationCode);
            log.info("Verification Code: {}", verificationCode);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again.");
        }
    }
    private void sendSMSThread(UserDTO user, String verificationCode) {
        CompletableFuture.runAsync(() -> smsUtil.sendSMS(user.getPhone(), "From: Spiritual Center \nVerification code\n" + verificationCode));
    }

    @Override
    public User verifyCode(String username, String code) {
        if(isVerificationCodeExpired(code)) throw new APIException("This code has expired. Please login again.");
        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            User userByUsername = jdbc.queryForObject(SELECT_USER_BY_USERNAME_QUERY, Map.of("username", username), new UserRowMapper());
            if(userByCode.getUsername().equalsIgnoreCase(userByUsername.getUsername())) {
                jdbc.update(DELETE_CODE, Map.of("code", code));
                return userByCode;
            } else {
                throw new APIException("Code is invalid. Please try again.");
            }
        } catch (EmptyResultDataAccessException exception) {
            throw new APIException("Could not find record");
        } catch (Exception exception) {
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public void resetPassword(String email) {
        if(getEmailCount(email.trim().toLowerCase()) <= 0) throw new APIException("There is no account for this email address.");
        try {
            String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
            User user = getUserByEmail(email);
            String verificationUrl = getVerificationURL(UUID.randomUUID().toString(), PASSWORD.getType());
            jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, Map.of("userId",  user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, Map.of("userId",  user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            sendEmail(user.getUsername(), user.getEmail(), verificationUrl, PASSWORD);
            log.info("Verification URL: {}", verificationUrl);
        } catch (Exception exception) {
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        if(isLinkExpired(key, PASSWORD)) throw new APIException("This link has expired. Please reset your password again.");
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, Map.of("url", getVerificationURL(key, PASSWORD.getType())), new UserRowMapper());
            //jdbc.update("DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY", of("id", user.getId())); //If link can be used only one time
            return user;
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new APIException("This link is not valid. Please reset your password again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public void renewPassword(int userId, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new APIException("Passwords don't match. Please try again.");
        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_USER_ID_QUERY, Map.of("id", userId, "password", encoder.encode(password)));
            //jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId", userId));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyAccountKey(String key) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY, Map.of("url", getVerificationURL(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "id", user.getId()));
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new APIException("This link is not valid.");
        } catch (Exception exception) {
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public User updateUserDetails(UpdateForm user) {
        try {
            if (userRepoJpa.findAll().size()>=100) throw new APIException("Maximum 100 users reached");
            if (userRepoJpa.getUserByUsername(user.getUsername()).size() > 0 && userRepoJpa.getUserByUsername(user.getUsername()).get(0).getId() != user.getId()) throw new APIException("Username already exists");
            if (userRepoJpa.getUserByPhone(user.getPhone()).size() > 0 && userRepoJpa.getUserByPhone(user.getPhone()).stream().anyMatch(u -> ((User)u).getId() != user.getId() )) throw new APIException("Phone already exists");
            if (userRepoJpa.getUserByEmail(user.getEmail()).size() > 0 && userRepoJpa.getUserByEmail(user.getEmail()).stream().anyMatch(u -> ((User)u).getId() != user.getId() )) throw new APIException("Email already exists");

            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
            return get(user.getId());
        }catch (EmptyResultDataAccessException exception) {
            throw new APIException("No User found by id: " + user.getId());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public void updatePassword(int id, String currentPassword, String newPassword, String confirmNewPassword) {
        if(!newPassword.equals(confirmNewPassword)) { throw new APIException("Passwords don't match. Please try again."); }
        User user = get(id);
        if(encoder.matches(currentPassword, user.getPassword())) {
            try {
                jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, Map.of("userId", id, "password", encoder.encode(newPassword)));
            }  catch (Exception exception) {
                throw new APIException("An error occurred. Please try again.");
            }
        } else {
            throw new APIException("Incorrect current password. Please try again.");
        }
    }

    private String getVerificationURL(String key, String type){
        return fromCurrentContextPath().path("/user/verify/" + type.toLowerCase() + "/" + key).toUriString().replace("8080", "4200");
    }
    private Boolean isLinkExpired(String key, VerificationType password) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL, Map.of("url", getVerificationURL(key, password.getType())), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new APIException("This link is not valid. Please reset your password again");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            throw new APIException("This code is not valid. Please login again.");
        } catch (Exception exception) {
            throw new APIException("An error occurred. Please try again.");
        }
    }


    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }
    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()))
                .addValue("phone", user.getPhone())
                .addValue("username", user.getUsername());
    }
    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("phone", user.getPhone());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if(user == null){
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        } else {
            log.info("User {} found.", username);
            return new UserPrincipal(user, user.getRole());
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_USERNAME_QUERY, Map.of("username", username), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException e){
            throw new APIException("User not found with this username: "+username);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new APIException("An error occurred.");
        }
    }
    @Override
    public User getUserByEmail(String email) {
        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException e){
            throw new APIException("User not found with this email: "+email);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new APIException("An error occurred.");
        }
    }
    @Override
    public void updateAccountSettings(int userId, Boolean enabled, Boolean notLocked) {
        try {
            jdbc.update(UPDATE_USER_SETTINGS_QUERY, Map.of("userId", userId, "enabled", enabled, "notLocked", notLocked));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("An error occurred. Please try again.");
        }
    }

    @Override
    public User toggleMfa(String email) {
        User user = getUserByEmail(email);
        if(isBlank(user.getPhone())) { throw new APIException("You need a phone number to change Multi-Factor Authentication"); }
        user.setUsingMfa(!user.isUsingMfa());
        try {
            jdbc.update(TOGGLE_USER_MFA_QUERY, Map.of("email", email, "isUsingMfa", user.isUsingMfa()));
            return user;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new APIException("Unable to update Multi-Factor Authentication");
        }
    }


    /**IMAGE**/
    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        if(image.getSize() > 2_000_000){
            throw new APIException("You need a small image to update your profile");
        }
        if(image.getContentType().equals("image/jpeg") || image.getContentType().equals("image/png")){
            String userImageUrl = setUserImageUrl(user.getEmail());
            user.setPicture(userImageUrl);
            saveImage(user.getEmail(), image);
            jdbc.update(UPDATE_USER_IMAGE_QUERY, Map.of("imageUrl", userImageUrl, "id", user.getId()));
        } else {
            throw new APIException("Only Jpeg and Png image are supported");
        }
    }

    private String setUserImageUrl(String email) {
        return fromCurrentContextPath().path("/api/user/image/" + email + ".png").toUriString();
    }

    private void saveImage(String email, MultipartFile image) {
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();
        //Path fileStorageLocation = Paths.get("/var/www/spiritualcenter/images/").toAbsolutePath().normalize();
        if(!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                throw new APIException("Unable to create directories to save image");
            }
            log.info("Created directories: {}", fileStorageLocation);
        }
        try {
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(email + ".png"), REPLACE_EXISTING);
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new APIException(exception.getMessage());
        }
        log.info("File saved in: {} folder", fileStorageLocation);
    }
}