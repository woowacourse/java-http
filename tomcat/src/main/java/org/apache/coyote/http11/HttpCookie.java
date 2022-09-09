package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpCookie() {
        this.values = new HashMap<>();
    }

    public void addCookie(final String cookieValue) {
        Map<String, String> cookies = Arrays.stream(cookieValue.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[COOKIE_KEY_INDEX], cookie -> cookie[COOKIE_VALUE_INDEX]));

        values.putAll(cookies);
    }

    public boolean hasJSessionId() {
        return values.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return values.get(JSESSIONID);
    }

    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(values);
    }
}
