package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public void addJSessionId(String jSessionId) {
        cookies.put(JSESSIONID_KEY, jSessionId);
    }

    public String getJSessionId() {
        return JSESSIONID_KEY + KEY_VALUE_DELIMITER + cookies.get(JSESSIONID_KEY);
    }
}
