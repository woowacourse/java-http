package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyHttpCookie {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    public MyHttpCookie(final String cookies) {
        this.values = new HashMap<>();
        if (cookies == null) {
            return;
        }
        for (String each : cookies.split(COOKIE_SEPARATOR)) {
            String[] keyAndValue = each.trim().split(KEY_VALUE_SEPARATOR, 2);
            values.put(keyAndValue[0], keyAndValue[1]);
        }
    }

    private boolean hasJSessionId() {
        return values.containsKey(JSESSIONID);
    }

    private String getJSessionId() {
        return values.get(JSESSIONID);
    }

    public String getOrCreateJSessionId() {
        if (!hasJSessionId()) {
            return UUID.randomUUID().toString();
        }
        return getJSessionId();
    }
}
