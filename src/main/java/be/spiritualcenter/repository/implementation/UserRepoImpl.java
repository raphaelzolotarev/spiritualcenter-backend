package be.spiritualcenter.repository.implementation;

import be.spiritualcenter.domain.UserPrincipal;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.exception.APIException;
import be.spiritualcenter.enums.Role;
import be.spiritualcenter.domain.User;
import be.spiritualcenter.repository.UserRepo;
import be.spiritualcenter.rowmapper.UserRowMapper;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static be.spiritualcenter.enums.VerificationType.ACCOUNT;
import static be.spiritualcenter.query.UserQuery.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;

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

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {
        if (getEmailCount(user.getEmail().trim().toLowerCase())>0) throw new APIException("Email already exists");
        try{
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource params = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, params, holder);
            user.setId(holder.getKey().intValue());
            user.setRole(Role.USER);
            String verificationURL = getVerificationURL(UUID.randomUUID().toString(), ACCOUNT.getType());
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId",user.getId(), "url", verificationURL));
            //emailService.sendVerificationUrl(user.getUsername(), user.getEmail(), verificationURL, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            return user;
        } catch (Exception e){
            log.error(e.getMessage());
            throw new APIException("An error occurred.");
        }
    }




    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();
        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, Map.of("id", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
            sendSMS(user.getPhone(), "From: SecureCapita \nVerification code\n" + verificationCode);
            log.info("Verification Code: {}", verificationCode);
        } catch (Exception exception) {
            log.error(exception.getMessage());
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
    private String getVerificationURL(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
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
}




