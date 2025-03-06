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
import be.spiritualcenter.repository.UserRepo;
import be.spiritualcenter.repository.implementation.UserRepoImpl;
import be.spiritualcenter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static be.spiritualcenter.dtomapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo<User> userRepo;
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
    public void renewPassword(String key, String pass, String confirmPass) {
        userRepo.renewPassword(key, pass, confirmPass);
    }

    private UserDTO mapToUserDTO(User user){
        return fromUser(user);
    }
}
