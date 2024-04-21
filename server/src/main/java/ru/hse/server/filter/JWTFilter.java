package ru.hse.server.filter;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.hse.server.service.JWTProviderService;
import ru.hse.server.utils.JWTUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JWTProviderService jwtProviderService;

    private String getTokenFromRequest(HttpServletRequest request) {
        System.out.println(request.getHeader(AUTHORIZATION_HEADER));
        final String bearer = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        return null;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        System.out.println(token);
        if (token != null && jwtProviderService.validateAccessToken(token)) {
            System.out.println("token correct");
            final Claims claims = jwtProviderService.getClaims(token);

            final Authentication jwtTokenInfo = JWTUtils.generate(claims);
            jwtTokenInfo.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtTokenInfo);
        }

        filterChain.doFilter(request, response);
    }
}
