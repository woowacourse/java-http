package org.apache.coyote.http11.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = ";";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeaderLine) {
        this.cookies = parseCookie(cookieHeaderLine);
    }

    private Map<String, String> parseCookie(String cookieHeaderLine) {
        if (cookieHeaderLine == null || cookieHeaderLine.isBlank()) {
            return Collections.emptyMap();
        }
        return toMap(cookieHeaderLine);
    }

    private Map<String, String> toMap(String cookieHeaderLine) {
        Map<String, String> cookies = new LinkedHashMap<>();

        for (String cookie : cookieHeaderLine.split(COOKIE_DELIMITER)) {
            String[] keyAndValue = cookie.split(COOKIE_KEY_VALUE_DELIMITER);
            cookies.put(keyAndValue[KEY_INDEX].trim(), keyAndValue[VALUE_INDEX].trim());
        }

        return cookies;
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
