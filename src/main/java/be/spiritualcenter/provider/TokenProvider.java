package be.spiritualcenter.provider;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.User;
import be.spiritualcenter.domain.UserPrincipal;
import be.spiritualcenter.enums.Role;
import be.spiritualcenter.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ctc.wstx.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static be.spiritualcenter.dtomapper.UserDTOMapper.toUser;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String SPIRITUALCENTER = "SPIRITUALCENTER";
    private static final String ALL_LOGGED_USERS = "ALL_LOGGED_USERS";
    private static final String AUTHORITIES = "AUTHORITIES";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_000_000;
    private static final long REFESH_TOKEN_EXPIRATION_TIME = 432_000_000;
    private static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    @Value("${jwt.secret}")
    private String secret;
    private final UserService userService;



    /**CREATE**/
        // Access token: Issuer, audience, creation/expiration date, User object, permissions, token encryption
        public String createAccessToken(UserPrincipal userPrincipal) {
            return JWT.create().withIssuer(SPIRITUALCENTER).withAudience(ALL_LOGGED_USERS)
                    .withIssuedAt(new Date()).withSubject(userPrincipal.getUser().getId()+"").withArrayClaim(AUTHORITIES, getClaimsFromUser(userPrincipal))
                    .withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                    .sign(HMAC512(secret.getBytes()));
        }
        // Refresh token: Used only to request a new one when the previous one expires
        public String createRefreshToken(UserPrincipal userPrincipal) {
            return JWT.create().withIssuer(SPIRITUALCENTER).withAudience(ALL_LOGGED_USERS)
                    .withIssuedAt(new Date()).withSubject(userPrincipal.getUser().getId()+"")
                    .withExpiresAt(new Date(currentTimeMillis() + REFESH_TOKEN_EXPIRATION_TIME))
                    .sign(HMAC512(secret.getBytes()));
        }
        // Creates an authentication object based on a token
        public Authentication getAuthentication(int userId, List<GrantedAuthority> authorities, HttpServletRequest request) {
            UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(userService.getUserById(userId), null, authorities);
            userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            return userPasswordAuthToken;
        }


    /**ROLES**/
        // Retrieves the user's roles
        private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
            return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

        }
        // Retrieves the user's roles via TOKEN
        private String[] getClaimsFromToken(String token) {
            JWTVerifier verifier = getJWTVerifier();
            return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
        }
        // Converts the roles from the token into GrantedAuthority objects usable by Spring Security
        public List<GrantedAuthority>getAuthorities(String token) {
            String[] claims = getClaimsFromToken(token);
            return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());
        }


    /**CHECK**/
        // Checks if a token is valid
        public boolean isTokenValid(int userId, String token) {
            JWTVerifier verifier = getJWTVerifier();
            return !Objects.isNull(userId) && !isTokenExpired(verifier, token);
        }
        // Checks if the token is expired
        private boolean isTokenExpired(JWTVerifier verifier, String token) {
            Date expiration = verifier.verify(token).getExpiresAt();
            return expiration.before(new Date());
        }
        // Ensures that the token was created by the application and has not been tampered with
        private JWTVerifier getJWTVerifier() {
            JWTVerifier verifier;
            try {
                Algorithm algorithm = HMAC512(secret);
                verifier = JWT.require(algorithm).withIssuer(SPIRITUALCENTER).build();
            }catch (JWTVerificationException exception) { throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED); }
            return verifier;
        }
        // Verifies the token and returns the user's identity
        public int getSubject(String token, HttpServletRequest request) {
            try {
                return Integer.valueOf(getJWTVerifier().verify(token).getSubject());
            } catch (TokenExpiredException exception) {
                request.setAttribute("expiredMessage", exception.getMessage());
                throw exception;
            } catch (InvalidClaimException exception) {
                request.setAttribute("invalidClaim", exception.getMessage());
                throw exception;
            } catch (Exception exception) {
                throw exception;
            }
        }


}
