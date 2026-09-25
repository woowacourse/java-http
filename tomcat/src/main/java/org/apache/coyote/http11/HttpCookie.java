package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookieHeader) {
        final Map<String, String> values = new HashMap<>();
        if (cookieHeader == null) {
            return new HttpCookie(values);
        }
        for (final String cookie : cookieHeader.split(COOKIE_SEPARATOR)) {
            final String[] keyAndValue = cookie.split(KEY_VALUE_SEPARATOR, 2);
            if (keyAndValue.length == 2) {
                values.put(keyAndValue[0].trim(), keyAndValue[1].trim());
            }
        }
        return new HttpCookie(values);
    }

    public boolean hasJSessionId() {
        return values.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return values.get(JSESSIONID);
    }
}
