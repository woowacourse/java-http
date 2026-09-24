package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = new ConcurrentHashMap<>(cookies);
    }

    public static HttpCookie parse(String cookieLine) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieLine != null && cookieLine.startsWith("Cookie:")) {
            String cookieValue = cookieLine.substring("Cookie:".length()).trim();

            String[] cookiePairs = cookieValue.split(";");
            for (String cookiePair : cookiePairs) {
                String[] keyAndValues = cookiePair.trim().split("=", 2);
                String key = keyAndValues[0];
                String value = keyAndValues[1];

                cookies.put(key, value);
            }
        }

        return new HttpCookie(cookies);
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public String getJSessionId() {
        return cookies.get("JSESSIONID");
    }
}
