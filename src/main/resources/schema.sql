CREATE SCHEMA IF NOT EXISTS spiritualcenter;


SET NAMES 'UTF8MB4';
USE spiritualcenter;

CREATE TABLE IF NOT EXISTS User (
                      user_id INT PRIMARY KEY AUTO_INCREMENT,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      username VARCHAR(255) UNIQUE NOT NULL,
                      profile_picture VARCHAR(255) DEFAULT 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/1200px-Default_pfp.svg.png',
                      phone VARCHAR(255) UNIQUE NOT NULL,
                      enabled BOOLEAN DEFAULT FALSE,
                      non_locked BOOLEAN DEFAULT TRUE,
                      using_mfa BOOLEAN DEFAULT FALSE,
                      role ENUM('USER', 'ADMIN') NOT NULL

);
CREATE TABLE IF NOT EXISTS posts (
                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                    title VARCHAR(255) NOT NULL,
                                    content MEDIUMTEXT NOT NULL,
                                    date DATETIME(6) NOT NULL

);


CREATE TABLE IF NOT EXISTS AccountVerification (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      user_id INT NOT NULL,
                      url VARCHAR(255) NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES User (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                    CONSTRAINT UQ_AccountVerification_User_Id UNIQUE (user_id),
                    CONSTRAINT UQ_AccountVerification_Url UNIQUE (url)

);

CREATE TABLE IF NOT EXISTS ResetPasswordVerifications (
                                            id INT PRIMARY KEY AUTO_INCREMENT,
                                            user_id INT NOT NULL,
                                            url VARCHAR(255) NOT NULL,
                                            expiration_date DATETIME NOT NULL,
                                            FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                            CONSTRAINT UQ_ResetPasswordVerifications_User_Id UNIQUE (user_id),
                                            CONSTRAINT UQ_ResetPasswordVerifications_Url UNIQUE (url)
);
CREATE TABLE IF NOT EXISTS TwoFactorVerifications (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        user_id INT NOT NULL,
                                        code VARCHAR(10) NOT NULL,
                                        expiration_date DATETIME NOT NULL,
                                        FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                        CONSTRAINT UQ_TwoFactorVerifications_User_Id UNIQUE (user_id),
                                        CONSTRAINT UQ_TwoFactorVerifications_Code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS Events
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    type        VARCHAR(50) NOT NULL CHECK(type IN ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS', 'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE', 'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
    description VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_Events_Type UNIQUE (type)
);

CREATE TABLE IF NOT EXISTS UserEvents
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT NOT NULL,
    event_id   INT NOT NULL,
    device     VARCHAR(100) DEFAULT NULL,
    ip_address VARCHAR(100) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Events (id) ON DELETE RESTRICT ON UPDATE CASCADE
);