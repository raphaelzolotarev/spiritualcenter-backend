package be.spiritualcenter.domain;

import be.spiritualcenter.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class User { //
    private int id;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Phone cannot be empty")
    private String phone;
    private String picture;
    private boolean isEnabled;
    private boolean isNotLocked;
    private boolean isUsingMfa;
    private Role role;
}


