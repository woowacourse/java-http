package org.apache.coyote.http11;

import java.util.LinkedHashMap;

public class Headers {

    private static final String COOKIE = "Cookie";

    private final LinkedHashMap<String, String> values;
    private final HttpCookie httpCookie;

    public Headers() {
        this.values = new LinkedHashMap<>();
        this.httpCookie = new HttpCookie();
    }

    public void addHeader(final String key, final String value) {
        if (COOKIE.equals(key)) {
            httpCookie.addCookie(value.trim());
        }

        values.put(key, value);
    }

    public boolean hasHeader(final String key) {
        return values.containsKey(key);
    }

    public String getHeader(final String key) {
        return values.get(key);
    }

    public boolean hasJSessionId() {
        return httpCookie.hasJSessionId();
    }

    public String getJSessionId() {
        return httpCookie.getJSessionId();
    }

    public LinkedHashMap<String, String> getValues() {
        return new LinkedHashMap<>(values);
    }
}
