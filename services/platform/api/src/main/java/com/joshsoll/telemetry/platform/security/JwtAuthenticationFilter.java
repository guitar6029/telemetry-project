package com.joshsoll.telemetry.platform.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.joshsoll.telemetry.platform.auth.constants.JwtConstants;
import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.auth.service.JwtService;
import com.joshsoll.telemetry.platform.auth.service.TokenRevocationService;
import com.joshsoll.telemetry.platform.security.util.CookieUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRevocationService tokenRevocationService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            TokenRevocationService tokenRevocationService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Optional<String> accessToken = CookieUtils.getCookieValue(request, JwtConstants.COOKIE_NAME);

        if (accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = accessToken.get();

        if (!jwtService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenRevocationService.isRevoked(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID userId = jwtService.extractSubject(token);

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        PlatformRole platformRole = user.getPlatformRole();
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
