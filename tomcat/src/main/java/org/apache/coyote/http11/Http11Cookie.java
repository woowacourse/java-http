package org.apache.coyote.http11;

import java.util.UUID;

public record Http11Cookie(String key, String value) {

    public static Http11Cookie sessionCookie() {
        return new Http11Cookie("JSESSIONID", UUID.randomUUID().toString());
    }

    public boolean isSessionCookie() {
        return key.equals("JSESSIONID");
    }

    @Override
    public String toString() {
        return "%s=%s".formatted(key, value);
    }
}
