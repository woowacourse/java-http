package org.apache.coyote.http11;

import java.util.UUID;

public record Cookie(String key, String value) {

    public static Cookie sessionCookie() {
        return new Cookie("JSESSIONID", UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return "%s=%s".formatted(key, value);
    }
}
