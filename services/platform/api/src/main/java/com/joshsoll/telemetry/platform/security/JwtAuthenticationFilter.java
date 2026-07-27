package com.joshsoll.telemetry.platform.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "access_token";
    private static final String ROLE_PREFIX = "ROLE_";
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = null;
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                accessToken = cookie.getValue();
                break;
            }
        }

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.validateToken(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract sub and look up the user
        UUID sub = jwtService.extractSubject(accessToken);

        User user = userRepository.findById(sub).orElse(null);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        PlatformRole platformRole = jwtService.extractPlatformRole(accessToken);
        GrantedAuthority authority = new SimpleGrantedAuthority(ROLE_PREFIX + platformRole.name());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singletonList(authority));

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
