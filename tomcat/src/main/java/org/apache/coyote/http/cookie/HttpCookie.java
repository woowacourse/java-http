package org.apache.coyote.http.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";
    
    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = Map.copyOf(cookies);
    }

    public static HttpCookie parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader != null && !cookieHeader.trim().isEmpty()) {
            String[] cookiePairs = cookieHeader.split(";");

            for (String pair : cookiePairs) {
                String[] keyValue = pair.trim().split("=", 2);
                if (keyValue.length == 2) {
                    cookies.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        return new HttpCookie(cookies);
    }

    public static String createSetCookieHeader(String name, String value) {
        return name + "=" + value;
    }

    public static String generateJSessionId() {
        return UUID.randomUUID().toString();
    }

    public static HttpCookie empty() {
        return new HttpCookie(Map.of());
    }

    public String getValue(String name) {
        return cookies.get(name);
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }

    public Map<String, String> getAllCookies() {
        return cookies;
    }
}
