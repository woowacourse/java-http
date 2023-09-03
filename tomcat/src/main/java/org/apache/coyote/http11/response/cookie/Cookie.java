package org.apache.coyote.http11.response.cookie;

import java.util.UUID;

public class Cookie {
    private final String key;
    private final String value;

    public Cookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie from(final String value) {
        final String[] split = value.split("=");
        return new Cookie(split[0], split[1]);
    }

    public static Cookie generateJsessionId() {
        return new Cookie("JSESSIONID", UUID.randomUUID().toString());
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
