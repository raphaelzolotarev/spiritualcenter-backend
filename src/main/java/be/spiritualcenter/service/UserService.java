package be.spiritualcenter.service;

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;

import javax.swing.*;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public interface UserService {
    UserDTO createUser(User user);
    UserDTO getUserByUsername(String username);
    void sendVerificationCode(UserDTO user);

    UserDTO verifyCode(String username, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    void renewPassword(String key, String pass, String confirmPass);

    UserDTO verifyAccountKey(String key);
}
