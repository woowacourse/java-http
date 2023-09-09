package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookie) {
        if (cookie.isEmpty()) {
            return new HttpCookie(new HashMap<>());
        }
        final String[] splitCookies = splitCookies(cookie);
        final Map<String, String> cookies = splitValues(splitCookies);
        return new HttpCookie(cookies);
    }

    private static String[] splitCookies(final String cookie) {
        return cookie.split(COOKIE_DELIMITER);
    }

    private static Map<String, String> splitValues(final String[] splitCookies) {
        final HashMap<String, String> cookies = new HashMap<>();
        for (final String cookie : splitCookies) {
            final String[] keyAndValue = cookie.split(COOKIE_VALUE_DELIMITER);
            cookies.put(keyAndValue[COOKIE_KEY_INDEX], keyAndValue[COOKIE_VALUE_INDEX]);
        }
        return cookies;
    }

    public String getValue(final String key) {
        return cookies.get(key);
    }
}
