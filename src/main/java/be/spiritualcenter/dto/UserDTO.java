package be.spiritualcenter.dto;

import be.spiritualcenter.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;
    private String email;
    private String username;
    private String phone;
    private String picture;
    private boolean isEnabled;
    private boolean isNotLocked;
    private boolean isUsingMfa;
    private Role role;
}
