package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_DELIMITER = "=";

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String value) {
        final Map<String, String> values = new HashMap<>();
        final String[] cookies = value.split(COOKIE_DELIMITER);
        for (String cookie : cookies) {
            final String[] split = cookie.split(COOKIE_KEY_DELIMITER);
            values.put(split[0], split[1]);
        }
        return new HttpCookie(values);
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public boolean containsSession() {
        return values.containsKey(SESSION_KEY);
    }

    public String getSessionId() {
        if (!values.containsKey(SESSION_KEY)) {
            return null;
        }
        return values.get(SESSION_KEY);
    }
}
