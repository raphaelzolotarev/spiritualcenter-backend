package be.spiritualcenter.query;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public class UserQuery {
    public static final String INSERT_USER_QUERY = "INSERT INTO User (email, phone, username, password, role) VALUES (:email, :phone, :username, :password, 'USER')";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM User WHERE email = :email";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerification (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_USERNAME_QUERY = "SELECT * FROM User WHERE username = :username";
    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM User WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM TwoFactorVerifications WHERE user_id = :id";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO TwoFactorVerifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
    public static final String SELECT_USER_BY_USER_CODE_QUERY = "SELECT * FROM User WHERE user_id = (SELECT user_id FROM TwoFactorVerifications WHERE code = :code)";
    public static final String DELETE_CODE = "DELETE FROM TwoFactorVerifications WHERE code = :code";
    public static final String SELECT_CODE_EXPIRATION_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM TwoFactorVerifications WHERE code = :code";
    public static final String DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY = "DELETE FROM ResetPasswordVerifications WHERE user_id = :userId";
    public static final String INSERT_PASSWORD_VERIFICATION_QUERY = "INSERT INTO ResetPasswordVerifications (user_id, url, expiration_date) VALUES (:userId, :url, :expirationDate)";
    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY = "SELECT * FROM User WHERE user_id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String SELECT_EXPIRATION_BY_URL = "SELECT expiration_date < NOW() AS is_expired FROM ResetPasswordVerifications WHERE url = :url";
    public static final String UPDATE_USER_PASSWORD_BY_URL_QUERY = "UPDATE User SET password = :password WHERE user_id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String DELETE_VERIFICATION_BY_URL_QUERY = "DELETE FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_ACCOUNT_URL_QUERY = "SELECT * FROM User WHERE user_id = (SELECT user_id FROM AccountVerification WHERE url = :url)";
    public static final String UPDATE_USER_ENABLED_QUERY = "UPDATE User SET enabled = :enabled WHERE user_id = :id";
    public static final String UPDATE_USER_DETAILS_QUERY = "UPDATE User SET username = :username, email = :email, phone = :phone WHERE user_id = :id";
    public static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM User WHERE user_id = :id";;
    public static final String UPDATE_USER_PASSWORD_BY_ID_QUERY = "UPDATE User SET password = :password WHERE user_id = :userId";
    public static final String UPDATE_USER_SETTINGS_QUERY = "UPDATE User SET enabled = :enabled, non_locked = :notLocked WHERE user_id = :userId";
    public static final String TOGGLE_USER_MFA_QUERY = "UPDATE User SET using_mfa = :isUsingMfa WHERE email = :email";
    public static final String UPDATE_USER_IMAGE_QUERY = "UPDATE User SET profile_picture = :imageUrl WHERE user_id = :id";
    public static final String UPDATE_USER_PASSWORD_BY_USER_ID_QUERY = "UPDATE User SET password = :password WHERE user_id = :id";

}
