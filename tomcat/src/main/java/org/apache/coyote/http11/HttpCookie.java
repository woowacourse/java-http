package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String EMPTY_VALUE = "";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_AND_VALUE_DELIMITER = "=";
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie() {
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public boolean isExistCookie() {
        return !cookies.isEmpty();
    }

    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }

    public void addJSessionId(String identifier) {
        cookies.put(JSESSIONID, identifier);
    }

    public String getJSessionId() {
        return cookies.getOrDefault(JSESSIONID, EMPTY_VALUE);
    }

    public void addCookies(String value) {
        String[] inputCookies = value.split(COOKIE_DELIMITER);
        for (String cookie : inputCookies) {
            String[] keyAndValues = cookie.split(KEY_AND_VALUE_DELIMITER);
            cookies.put(keyAndValues[0], keyAndValues[1]);
        }
    }
}
