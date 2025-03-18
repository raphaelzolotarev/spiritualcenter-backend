package be.spiritualcenter.repository;


/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepoJpa extends JpaRepository<User, Integer> {
    Page<User> findByUsernameContaining(String name, Pageable pageable);

    boolean findUserByEmail(@Email(message = "Invalid email") String email);

    boolean findUserByPhone(String phone);

    boolean findUserByUsername(String username);

    List<User> getUserByUsername(String username);

    Collection<Object> getUserByPhone(String phone);

    Collection<Object> getUserByEmail(@Email(message = "Invalid email") String email);
}

