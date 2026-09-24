package org.apache.coyote.http11;

import java.util.Optional;

public record Cookie(String name, String value) {

    public static Optional<Cookie> parse(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String[] parts = text.split("=", 2);
        if (parts.length != 2 || parts[0].isBlank()) {
            return Optional.empty();
        }

        return Optional.of(new Cookie(parts[0].trim(), parts[1].trim()));
    }
}
