package be.spiritualcenter.repository;

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.form.UpdateForm;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collection;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public interface UserRepo <T extends User> {
    T create(T user);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    Collection<T> list(int page, int pageSize);

    T get(int id);

    void sendVerificationCode(UserDTO user);

    User verifyCode(String username, String code);

    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void renewPassword(int key, String pass, String confirmPass);

    T verifyAccountKey(String key);

    T updateUserDetails(UpdateForm user);

    void updatePassword(int id, String currentPassword, String newPassword, String confirmNewPassword);

    User toggleMfa(String email);

    void updateImage(UserDTO user, MultipartFile image);

    void updateAccountSettings(int userId, Boolean enabled, Boolean notLocked);
}