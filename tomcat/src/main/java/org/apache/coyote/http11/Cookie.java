package org.apache.coyote.http11;

import java.util.Objects;

public class Cookie {

    public static final String J_SESSION_KEY = "JSESSIONID=";

    private final String value;

    public Cookie(final String cookie) {
        this.value = cookie;
    }

    public boolean isJSessionCookie() {
        if (value == null) {
            return false;
        }
        return value.contains(J_SESSION_KEY);
    }

    public String getJSessionId() {
        return value.split("=")[1];
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Cookie cookie)) {
            return false;
        }
        return Objects.equals(value, cookie.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
