package be.spiritualcenter.constants;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

public class Constants {
    public static final String[] PUBLIC_URLS = {
            "/api/user/login/**",
            "/api/user/register/**",
            "/api/user/verify/code/**",
            "/api/user/resetpassword/**",
            "/api/user/verify/password/**",
            "/api/user/verify/account/**",
            "/api/user/refresh/token/**",
            "/api/user/image/**",
            "/api/user/new/password/**"
    };
    public static final String[] PUBLIC_ROUTES = {
            "/api/user/login",
            "/api/user/register",
            "/api/user/verify/code",
            "/api/user/refresh/token",
            "/api/user/image",
            "/api/user/new/password"
    };
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    public static final String SPIRITUALCENTER = "SPIRITUALCENTER";
    public static final String ALL_LOGGED_USERS = "ALL_LOGGED_USERS";
    public static final String AUTHORITIES = "AUTHORITIES";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_000_000;
    public static final long REFESH_TOKEN_EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
}