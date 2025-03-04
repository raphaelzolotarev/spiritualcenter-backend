package be.spiritualcenter.rowmapper;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import be.spiritualcenter.enums.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt(1))
                .email(rs.getString(2))
                .password(rs.getString(3))
                .username(rs.getString(4))
                .picture(rs.getString(5))
                .phone(rs.getString(6))
                .isEnabled(rs.getBoolean(7))
                .isNotLocked(rs.getBoolean(8))
                .isUsingMfa(rs.getBoolean(9))
                .role(Role.valueOf(rs.getString(10).toUpperCase()))
                .build();
    }
}














