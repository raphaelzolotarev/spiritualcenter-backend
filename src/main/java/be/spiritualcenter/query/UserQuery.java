package be.spiritualcenter.query;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public class UserQuery {
    public static final String INSERT_USER_QUERY = "INSERT INTO User (email, phone, username, password) VALUES (:email, :phone, :username, :password)";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM User WHERE email = :email";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerification (user_id, url) VALUES (:userId, :url)";
}
