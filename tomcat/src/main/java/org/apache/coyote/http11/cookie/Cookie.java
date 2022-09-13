package org.apache.coyote.http11.cookie;

public class Cookie {

    private static final String KEY_VALUE_SEPARATOR = "=";

    private final String key;
    private final String value;

    public Cookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie from(final String rawCookie) {
        final int keyValueSeparatorIndex = rawCookie.indexOf(KEY_VALUE_SEPARATOR);
        final String cookieKey = rawCookie.substring(0, keyValueSeparatorIndex);
        final String cookieValue = rawCookie.substring(keyValueSeparatorIndex + 1);
        return new Cookie(cookieKey, cookieValue);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
