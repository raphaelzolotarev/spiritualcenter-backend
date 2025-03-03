package be.spiritualcenter.repository.implementation;

import be.spiritualcenter.exception.APIException;
import be.spiritualcenter.enums.Role;
import be.spiritualcenter.domain.User;
import be.spiritualcenter.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static be.spiritualcenter.enums.VerificationType.ACCOUNT;
import static be.spiritualcenter.query.UserQuery.*;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepoImpl implements UserRepo<User> {

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
}




