package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(cookies);
        }
        for (String pair : cookieHeader.split(COOKIE_SEPARATOR)) {
            String[] keyValue = pair.trim().split(KEY_VALUE_SEPARATOR, 2);
            cookies.put(keyValue[0], keyValue[1]);
        }
        return new HttpCookie(cookies);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
