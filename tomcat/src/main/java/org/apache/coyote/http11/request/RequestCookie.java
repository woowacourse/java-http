package org.apache.coyote.http11.request;

public class RequestCookie {

    private static final String KEY_VALUE_DELIMITER = "=";

    private final String key;
    private final String value;

    public RequestCookie(String rawCookie) {
        this.key = rawCookie.split(KEY_VALUE_DELIMITER)[0];
        this.value = rawCookie.split(KEY_VALUE_DELIMITER)[1];
    }

    public boolean hasKey(String key) {
        return this.key.equals(key);
    }

    public String getValue() {
        return this.value;
    }
}
