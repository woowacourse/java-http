package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(String cookieHeader) {
        Map<String, String> values = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(values);
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] nameAndValue = cookie.trim().split("=", 2);
            if (nameAndValue.length == 2) {
                values.put(nameAndValue[0], nameAndValue[1]);
            }
        }

        return new HttpCookie(values);
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public String get(String name) {
        return values.get(name);
    }

    public static String createJSessionId(String sessionId) {
        return JSESSIONID + "=" + sessionId;
    }
}
