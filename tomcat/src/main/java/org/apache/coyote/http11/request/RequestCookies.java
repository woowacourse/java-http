package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestCookies {

    private static final RequestCookies EMPTY_COOKIES = new RequestCookies(Map.of());
    private static final String COOKIES_SEPARATOR = ";";
    private static final String COOKIE_SEPARATOR = "=";

    private final Map<String, String> properties;

    public RequestCookies(Map<String, String> values) {
        this.properties = values;
    }

    public static RequestCookies of(String httpCookies) {
        if (httpCookies == null || httpCookies.isBlank()) {
            return EMPTY_COOKIES;
        }
        String[] splitHttpCookies = httpCookies.split(COOKIES_SEPARATOR);
        Map<String, String> cookies = new HashMap<>();
        for (String cookie : splitHttpCookies) {
            if (!isValidCookie(cookie)) {
                continue;
            }
            int index = cookie.indexOf(COOKIE_SEPARATOR);
            String key = cookie.substring(0, index).trim();
            String value = cookie.substring(index + 1).trim();
            cookies.put(key, value);
        }
        return new RequestCookies(cookies);
    }

    private static boolean isValidCookie(String cookie) {
        int index = cookie.indexOf(COOKIE_SEPARATOR);
        int cookieLastIndex = cookie.length() - 1;
        return index != -1 && index != cookieLastIndex;
    }

    public String get(String key) {
        return properties.get(key);
    }
}
