package be.spiritualcenter.resource;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import be.spiritualcenter.domain.HttpResponse;
import be.spiritualcenter.domain.UserPrincipal;
import be.spiritualcenter.dto.UserDTO;
import be.spiritualcenter.exception.APIException;
import be.spiritualcenter.form.*;
import be.spiritualcenter.provider.TokenProvider;
import be.spiritualcenter.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.Authenticator;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static be.spiritualcenter.constants.Constants.TOKEN_PREFIX;
import static be.spiritualcenter.dtomapper.UserDTOMapper.toUser;
import static be.spiritualcenter.utils.ExceptionUtils.processError;
import static be.spiritualcenter.utils.UserUtils.getAuthenticatedUser;
import static be.spiritualcenter.utils.UserUtils.getLoggedInUser;
import static java.time.LocalTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;


@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;


    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        Authentication authentication = authenticate(loginForm.getUsername(), loginForm.getPassword());
        UserDTO user = getLoggedInUser(authentication);
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
    }
    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        UserDTO user = userService.getUserByUsername(getAuthenticatedUser(authentication).getUsername());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", user))
                        .message("Welcome to your profile")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private Authentication authenticate(String username, String password){
        try {
            Authentication authentication = authenticationManager.authenticate(unauthenticated(username, password));
            return authentication;
        } catch (Exception e){
            //processError(request, response, e);
            throw new APIException(e.getMessage());
        }
    }
        //LOGIN USER NO MFA
        private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of(
                                    "user", user,
                                    "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                    "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))
                            ))
                            .message("Welcome "+user.getUsername())
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
                            .data(Map.of(
                                    "user", user,
                                    "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                    "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))
                            ))
                            .message("Verification Code Sent")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByUsername(user.getUsername())), user.getRole());
    }

    //REGISTER
    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) throws InterruptedException {
        //TimeUnit.SECONDS.sleep(4);
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .message(String.format("User account created for user %s", user.getUsername()))
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }


    @GetMapping("/verify/code/{username}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("username") String username, @PathVariable("code") String code){
        UserDTO user = userService.verifyCode(username, code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user))
                                , "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO updatedUser = userService.updateUserDetails(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", updatedUser))
                        .message("Your profile is updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }


    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Email sent. Please check your email (and spam) to reset your password.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable("key") String key) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", user))
                        .message("Please enter a new password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PutMapping("/new/password")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@RequestBody @Valid NewPasswordForm form) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        userService.updatePassword(form.getUserId(), form.getPassword(), form.getConfirmPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password reset successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message(userService.verifyAccountKey(key).isEnabled() ? "Account already verified" : "Account verified")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request)  {
        if(isHeaderAndTokenValid(request)){
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user))
                                    , "refresh_token", token))
                            .message("Token refreshed")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        } else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .reason("Refresh Token missing or invalid")
                            .devMessage("Refresh Token missing or invalid")
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .build());
        }

    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(
                        tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                        request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
    }
    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(Authentication authentication, @RequestBody @Valid SettingsForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateAccountSettings(userDTO.getId(), form.getEnabled(), form.getNotLocked());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of("user", userService.getUserById(userDTO.getId())))
                        .timeStamp(now().toString())
                        .message("Account settings updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }



    @PatchMapping("/togglemfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO user = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of("user", user))
                        .timeStamp(now().toString())
                        .message("Multi-Factor Authentication updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PatchMapping("/update/image")
    public ResponseEntity<HttpResponse> updateProfileImage(Authentication authentication, @RequestParam("image") MultipartFile image) throws InterruptedException {
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updateImage(user, image);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of("user", userService.getUserById(user.getId())))
                        .timeStamp(now().toString())
                        .message("Your profile image is updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
    }

    @GetMapping("/numberusers")
    public ResponseEntity<HttpResponse> getNumberOfUsers() {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("nbr",  StreamSupport.stream(userService.getAllUsers().spliterator(), false).count() ))
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCustomer(@AuthenticationPrincipal UserDTO user, Optional<String> name, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) throws InterruptedException {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUsername(user.getUsername()),
                                "page", userService.searchUsers(name.orElse(""), page.orElse(0), size.orElse(10))))
                        .message("Here are your search results")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}







