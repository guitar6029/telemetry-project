package com.joshsoll.telemetry.platform.security.util;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class CookieUtils {
    private CookieUtils() {
    }

    public static Optional<String> getCookieValue(
            HttpServletRequest request,
            String cookieName) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }

        return Optional.empty();

    }

}
