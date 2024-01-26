package com.speer.authserver.util;


import com.speer.authserver.exception.AuthorizationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt != null) {
                if (jwtTokenProvider.validateToken(jwt)) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            null, null, jwtTokenProvider.getAuthoritiesFromToken(jwt)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Token is not valid, handle accordingly (e.g., throw an exception or log)
                    log.warn("Invalid JWT token");
                }
            } else {
                throw new AuthorizationException("Invalid or no authorization token was provided");
            }
        } catch (Exception ex) {
            log.info("An Exception Occurred", ex);
            throw new AuthorizationException("An error occurred while validating token");
        }

        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/auth/");
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        // Extract JWT from the Authorization header or from a custom header
        // (Adjust this based on how you send the JWT in your requests)
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

