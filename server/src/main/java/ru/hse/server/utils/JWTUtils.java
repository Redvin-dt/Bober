package ru.hse.server.utils;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.hse.server.configuration.JWTAuthentication;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTUtils {
    public static JWTAuthentication generate(Claims claims) {
        final JWTAuthentication jwtInfoToken = new JWTAuthentication();
        jwtInfoToken.setUserLogin(claims.getSubject());
        return jwtInfoToken;
    }
}
