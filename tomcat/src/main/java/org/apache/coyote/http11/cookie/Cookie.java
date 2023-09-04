package org.apache.coyote.http11.cookie;

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
