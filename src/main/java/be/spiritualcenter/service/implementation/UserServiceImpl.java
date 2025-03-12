package be.spiritualcenter.service.implementation;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.dtomapper.UserDTOMapper;
import be.spiritualcenter.form.UpdateForm;
import be.spiritualcenter.repository.UserRepo;
import be.spiritualcenter.repository.UserRepoJpa;
import be.spiritualcenter.repository.implementation.UserRepoImpl;
import be.spiritualcenter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static be.spiritualcenter.dtomapper.UserDTOMapper.fromUser;
import static org.springframework.data.domain.PageRequest.of;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo<User> userRepo;
    private final UserRepoJpa userRepoJpa;
    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepo.create(user));
    }
    @Override
    public UserDTO getUserByUsername(String username) {
        return mapToUserDTO(userRepo.getUserByUsername(username));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepo.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String username, String code) {
        return mapToUserDTO(userRepo.verifyCode(username, code));
    }

    @Override
    public void resetPassword(String email) {
        userRepo.resetPassword(email);
    }

    @Override
    public UserDTO verifyPasswordKey(String key) {
        return mapToUserDTO(userRepo.verifyPasswordKey(key));
    }

    @Override
    public void updatePassword(int userId, String pass, String confirmPass) {
        userRepo.renewPassword(userId, pass, confirmPass);
    }

    @Override
    public UserDTO verifyAccountKey(String key) {
        return mapToUserDTO(userRepo.verifyAccountKey(key));
    }

    @Override
    public UserDTO updateUserDetails(UpdateForm user) {
        return mapToUserDTO(userRepo.updateUserDetails(user));
    }

    @Override
    public UserDTO getUserById(int userId) {
        return mapToUserDTO(userRepo.get(userId));
    }

    @Override
    public void updatePassword(int id, String currentPassword, String newPassword, String confirmNewPassword) {
        userRepo.updatePassword(id, currentPassword, newPassword, confirmNewPassword);
    }

    @Override
    public void updateAccountSettings(int userId, Boolean enabled, Boolean notLocked) {
        userRepo.updateAccountSettings(userId, enabled, notLocked);
    }
    @Override
    public UserDTO toggleMfa(String email) {
        return mapToUserDTO(userRepo.toggleMfa(email));
    }

    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        userRepo.updateImage(user, image);
    }

    private UserDTO mapToUserDTO(User user){
        return fromUser(user);
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        return userRepoJpa.findAll(of(page, size));
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepoJpa.findAll();
    }

    @Override
    public Page<User> searchUsers(String name, int page, int size) {
        return userRepoJpa.findByUsernameContaining(name, of(page, size));
    }
}
