package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String requestCookieLine) {
        if (requestCookieLine == null) {
            return;
        }
        for (String cookie : requestCookieLine.split(COOKIE_SEPARATOR)) {
            String[] cookieKeyValue = cookie.split(COOKIE_KEY_VALUE_SEPARATOR);
            cookies.put(cookieKeyValue[0].trim(), cookieKeyValue[1].trim());
        }
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
