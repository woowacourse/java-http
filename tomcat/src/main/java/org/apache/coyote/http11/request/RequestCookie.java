package org.apache.coyote.http11.request;

public class RequestCookie {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int INDEX_OF_KEY = 0;
    private static final int INDEX_OF_VALUE = 1;

    private final String key;
    private final String value;

    public RequestCookie(String rawCookie) {
        this.key = rawCookie.split(KEY_VALUE_DELIMITER)[INDEX_OF_KEY];
        this.value = rawCookie.split(KEY_VALUE_DELIMITER)[INDEX_OF_VALUE];
    }

    public boolean hasKey(String key) {
        return this.key.equals(key);
    }

    public String getValue() {
        return this.value;
    }
}
