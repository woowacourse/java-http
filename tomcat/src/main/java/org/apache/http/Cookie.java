package org.apache.http;

import java.util.List;

public class Cookie {

    private static final String COOKIE_DELIMITER_REGEX = "=";
    private static final int COOKIE_DIVIDED_LIMIT = 2;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;
    private static final String JSESSIONID_KEY = "JSESSIONID";

    private final String key;
    private final String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie from(String cookie) {
        List<String> dividedCookie = List.of(
            cookie.split(COOKIE_DELIMITER_REGEX, COOKIE_DIVIDED_LIMIT));
        String key = dividedCookie.get(COOKIE_KEY_INDEX);
        String value = dividedCookie.get(COOKIE_VALUE_INDEX);
        return new Cookie(key, value);
    }

    public static Cookie fromByJSessionId(String id) {
        return new Cookie(JSESSIONID_KEY, id);
    }

    public boolean isJSessionCookie() {
        return JSESSIONID_KEY.equals(this.key);
    }

    public String getValue() {
        return value;
    }
}
