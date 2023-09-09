package org.apache.coyote.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie of(String cookieString) {
        Map<String, String> cookies = new LinkedHashMap<>();

        if (cookieString != null && !cookieString.isEmpty()) {
            String[] cookiePairs = cookieString.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.split("=");
                if (parts.length == 2) {
                    String name = parts[0];
                    String value = parts[1];
                    cookies.put(name, value);
                }
            }
        }

        return new HttpCookie(cookies);
    }

    public String getValue(String key) {
        return cookie.get(key);
    }

    public String convertToHeader() {
        return cookie.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "cookie=" + cookie +
                '}';
    }
}
