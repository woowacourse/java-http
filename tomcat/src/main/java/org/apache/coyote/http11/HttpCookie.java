package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_EQUAL = "=";
    private final Map<String, String> cookies;

    public HttpCookie(final String cookie) {
        this.cookies = parseCookie(cookie);
    }

    private Map<String, String> parseCookie(final String cookie) {
        final Map<String, String> cookies = new LinkedHashMap<>();
        final String[] values = cookie.split(COOKIE_DELIMITER);
        for (String value : values) {
            final String[] parameterAndValue = value.split(COOKIE_EQUAL);
            cookies.put(parameterAndValue[0], parameterAndValue[1]);
        }
        return cookies;
    }

    public String getCookieValue(final String parameter) {
        if (cookies.isEmpty() || !cookies.containsKey(parameter)) {
            return "";
        }
        return cookies.get(parameter);
    }
}
