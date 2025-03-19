package be.spiritualcenter.domain;

import be.spiritualcenter.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.Email;
import static jakarta.persistence.GenerationType.IDENTITY;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Email(message = "Invalid email")
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_picture")
    private String picture;

    @Column(name = "enabled")
    private boolean isEnabled;

    @Column(name = "non_locked")
    private boolean isNotLocked;

    @Column(name = "using_mfa")
    private boolean isUsingMfa;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}