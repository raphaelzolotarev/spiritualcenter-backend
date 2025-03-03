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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo<User> userRepo;
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepo.create(user));
    }
}
