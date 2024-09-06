package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE_REGEX = "; ";
    private static final String KEY_VALUE_REGEX = "=";

    private Map<String, String> cookies;

    public HttpCookie() {
        this(new HashMap<>());
    }

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public HttpCookie(String cookie) {
        this(parseCookie(cookie));
    }

    private static Map<String, String> parseCookie(String cookie) {
        Map<String, String> map = new HashMap<>();
        String[] parts = cookie.split(COOKIE_REGEX);
        for (String part : parts) {
            String[] keyValue = part.trim().split(KEY_VALUE_REGEX);
            if (keyValue.length == 2) {
                map.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
            }
        }
        return map;
    }

    public void addSessionId() {
        cookies.put(JSESSIONID, String.valueOf(UUID.randomUUID()));
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID);
    }

    public String getCookieValue(String name) {
        return cookies.get(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(COOKIE_REGEX);
            }
            sb.append(entry.getKey())
                    .append(KEY_VALUE_REGEX)
                    .append(entry.getValue());
        }
        return sb.toString();
    }
}
