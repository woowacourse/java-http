package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Cookie {

    private static final String COOKIE_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    private Http11Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Http11Cookie from(String cookies) {
        if (cookies.isBlank()) {
            return new Http11Cookie(Map.of());
        }

        Map<String, String> cookieMap = Arrays.stream(cookies.split(COOKIE_DELIMITER))
                .map(String::trim)
                .map(cookie -> cookie.split(KEY_VALUE_DELIMITER, KEY_VALUE_SIZE))
                .collect(Collectors.toMap(cookie -> cookie[KEY_INDEX], cookie -> cookie[VALUE_INDEX]));

        return new Http11Cookie(cookieMap);
    }

    public boolean isJSessionIdEmpty() {
        return !cookies.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
