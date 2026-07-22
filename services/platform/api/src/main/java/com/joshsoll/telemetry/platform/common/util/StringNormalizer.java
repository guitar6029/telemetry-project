package com.joshsoll.telemetry.platform.common.util;

import java.util.Locale;

public final class StringNormalizer {
    private StringNormalizer() {
    }

    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
