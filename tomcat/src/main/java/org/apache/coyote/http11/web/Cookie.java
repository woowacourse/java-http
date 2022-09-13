package org.apache.coyote.http11.web;

import java.util.Objects;

public class Cookie {

    private final String key;
    private final String value;

    public Cookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String toPair() {
        return key + "=" + value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Cookie cookie = (Cookie) o;
        return Objects.equals(key, cookie.key) && Objects.equals(value, cookie.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
