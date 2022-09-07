package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    public HttpCookie() {
        this.values = new HashMap<>();
    }

    public void addCookie(final String cookieValue) {
        Map<String, String> cookies = Arrays.stream(cookieValue.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));

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
