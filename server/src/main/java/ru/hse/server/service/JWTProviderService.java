package ru.hse.server.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JWTProviderService {
    final static Logger logger = LoggerFactory.getLogger(JWTProviderService.class);

    private final SecretKey jwtAccessSecret;

    public JWTProviderService(@Value("${jwt.access.secret}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    public String generateAccessToken(@Nonnull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder().setSubject(user.getUserLogin()).setExpiration(accessExpiration).signWith(jwtAccessSecret).compact();
    }

    public boolean validateAccessToken(@Nonnull String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtAccessSecret).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token expired", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported jwt {}", token, e);
        } catch (MalformedJwtException e) {
            logger.error("Malformed jwt {}", token, e);
        } catch (SignatureException e) {
            logger.error("Invalid signature {}", token, e);
        } catch (Exception e) {
            logger.error("Invalid token {}", token, e);
        }

        return false;
    }

    public Claims getClaims(@Nonnull String token) {
        return Jwts.parserBuilder().setSigningKey(jwtAccessSecret).build().parseClaimsJws(token).getBody();
    }
}
