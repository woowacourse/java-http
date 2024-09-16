package org.apache.catalina.coockie;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpCookie {

    private static final String JSESSIONID_VALUE = "JSESSIONID";
    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpCookie(String rawCookies) {
        this.cookies = mapCookies(rawCookies);
    }

    private static Map<String, String> mapCookies(String rawCookies) {
        if (rawCookies == null || rawCookies.isBlank()) {
            return Map.of();
        }

        Map<String, String> cookieGroup = new HashMap<>();
        String[] cookiesElements = rawCookies.split(COOKIE_DELIMITER);
        for (String cookiesElement : cookiesElements) {
            String[] cookiePair = cookiesElement.split(COOKIE_VALUE_DELIMITER);
            if (cookiePair.length == 2) {
                cookieGroup.put(cookiePair[0], cookiePair[1]);
            }
        }
        return cookieGroup;
    }

    public boolean hasSessionId() {
        return cookies.containsKey(JSESSIONID_VALUE);
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID_VALUE);
    }

    public String getResponse() {
        if (cookies.isEmpty()) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        for (Entry<String, String> stringStringEntry : cookies.entrySet()) {
            response.append(stringStringEntry.getKey())
                    .append(COOKIE_VALUE_DELIMITER)
                    .append(stringStringEntry.getValue())
                    .append(";");
        }
        return String.valueOf(response);
    }
}
