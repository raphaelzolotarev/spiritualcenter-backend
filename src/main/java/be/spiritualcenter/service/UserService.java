package be.spiritualcenter.service;

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.form.UpdateForm;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

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

    void updatePassword(int userId, String pass, String confirmPass);

    UserDTO verifyAccountKey(String key);

    UserDTO updateUserDetails(UpdateForm user);

    UserDTO getUserById(int userId);

    void updatePassword(int userId, String currentPassword, String newPassword, String confirmNewPassword);

    void updateAccountSettings(int userId, Boolean enabled, Boolean notLocked);

    UserDTO toggleMfa(String email);

    void updateImage(UserDTO user, MultipartFile image);

    Iterable<User> getAllUsers();

    Page<User> searchUsers(String name, int page, int size, String type, String order);

    void deleteUserById(int id);
}
