package be.spiritualcenter.service;

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;
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

    User getUser(String username);
}
