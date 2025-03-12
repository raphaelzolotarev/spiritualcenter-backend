package be.spiritualcenter.constants;


/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

public class Constants {
    public static final String[] PUBLIC_URLS = {
            "/user/login/**",
            "/user/register/**",
            "/user/verify/code/**",
            "/user/resetpassword/**",
            "/user/verify/password/**",
            "/user/verify/account/**",
            "/user/refresh/token/**",
            "/user/image/**",
            "/user/new/password/**"
    };
    public static final String[] PUBLIC_ROUTES = {
            "/user/login",
            "/user/register",
            "/user/verify/code",
            "/user/refresh/token",
            "/user/image",
            "/user/new/password"
    };
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HTTP_OPTIONS_METHOD = "OPTIONS";

    public static final String SPIRITUALCENTER = "SPIRITUALCENTER";
    public static final String ALL_LOGGED_USERS = "ALL_LOGGED_USERS";
    public static final String AUTHORITIES = "AUTHORITIES";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_000_000;
    public static final long REFESH_TOKEN_EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

}
