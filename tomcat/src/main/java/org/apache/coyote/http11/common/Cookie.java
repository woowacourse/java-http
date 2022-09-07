package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final String COOKIE_HEADER_DELIMITER = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie parse(String cookieHeader) {
        Map<String, String> cookieHeaders = new HashMap<>();
        if (cookieHeader.isBlank()) {
            return new Cookie(cookieHeaders);
        }

        String[] cookies = cookieHeader.split(COOKIE_HEADER_DELIMITER);
        for (String cookie : cookies) {
            String[] splitCookie = cookie.split(KEY_VALUE_SEPARATOR);
            cookieHeaders.put(splitCookie[KEY_INDEX], splitCookie[VALUE_INDEX]);
        }

        return new Cookie(cookieHeaders);
    }

    public boolean isContainJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }
}
