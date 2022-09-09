package org.apache.coyote.http11.session;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String JSESSIONID = "JSESSIONID";
    private final Map<String, Object> cookies;

    private Cookie(Map<String, Object> cookies) {
        this.cookies = cookies;
    }

    public static Cookie of(String cookieValue) {
        Map<String, Object> cookies = new LinkedHashMap<>();
        String[] cookieValues = cookieValue.split("; ");
        for (String cookie : cookieValues) {
            putCookie(cookies, cookie);
        }

        return new Cookie(cookies);
    }

    private static void putCookie(Map<String, Object> cookies, String cookie) {
        int index = cookie.indexOf("=");
        if (index != -1) {
            String key = cookie.substring(0, index);
            String value = cookie.substring(index + 1);
            cookies.put(key, value);
        }
    }

    public static Cookie fromJSessionId(String jSessionId) {
        return new Cookie(Map.of(JSESSIONID, jSessionId));
    }

    public Object getJSessionId() {
        return cookies.get(JSESSIONID);
    }

    @Override
    public String toString() {
        return cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().toString())
                .collect(Collectors.joining("; "));
    }
}
