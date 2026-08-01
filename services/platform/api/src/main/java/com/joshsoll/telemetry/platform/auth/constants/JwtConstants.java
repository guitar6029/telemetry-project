package com.joshsoll.telemetry.platform.auth.constants;

public class JwtConstants {

    public static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 10;
    public static final String PLATFORM_ROLE_CLAIM = "platformRole";
    public static final String COOKIE_NAME = "access_token";

    private JwtConstants() {
    }
}
