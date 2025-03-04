package be.spiritualcenter.repository;

import be.spiritualcenter.domain.User;
import be.spiritualcenter.dto.UserDTO;

import java.util.Collection;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public interface UserRepo <T extends User>{
    T create(T user);
    User getUserByUsername(String username);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);
    void sendVerificationCode(UserDTO user);
}
