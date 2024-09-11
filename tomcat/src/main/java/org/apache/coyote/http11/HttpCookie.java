package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_PAIR_DELIMITER = ";";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;
    private static final int COOKIE_PAIR_SPLIT_LIMIT = 2;

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = new HashMap<>();
        parseCookies(cookieHeader);
    }

    private void parseCookies(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return;
        }

        for (String cookiePair : cookieHeader.split(COOKIE_PAIR_DELIMITER)) {
            String[] keyValue = cookiePair.trim().split(COOKIE_KEY_VALUE_DELIMITER, COOKIE_PAIR_SPLIT_LIMIT);
            setCookie(keyValue);
        }
    }

    private void setCookie(String[] keyValue) {
        if (keyValue.length == COOKIE_PAIR_SPLIT_LIMIT) {
            String key = keyValue[COOKIE_KEY_INDEX].trim();
            String value = keyValue[COOKIE_VALUE_INDEX].trim();
            cookies.put(key, value);
        }
    }

    public static String ofJSessionId(String value) {
        return JSESSIONID + COOKIE_KEY_VALUE_DELIMITER + value;
    }

    public String getJsessionid() {
        return getCookie(JSESSIONID);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public boolean containsJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "cookies=" + cookies +
                '}';
    }
}
