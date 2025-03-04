package be.spiritualcenter.resource;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import be.spiritualcenter.domain.HttpResponse;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.form.LoginForm;
import be.spiritualcenter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
        UserDTO user = userService.getUserByUsername(loginForm.getUsername());
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);

    }
        //LOGIN USER NO MFA
        private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of("user", user))
                            .message("Login successful")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        //LOGIN USER WITH MFA
        private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
            userService.sendVerificationCode(user);
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of("user", user))
                            .message("Verification Code Sent")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        }

    //REGISTER
    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .message("User created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }
}






