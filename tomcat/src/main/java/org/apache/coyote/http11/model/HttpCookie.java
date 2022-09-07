package org.apache.coyote.http11.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String COOKIE_LINE_SEPARATOR = "; ";
    public static final String COOKIE_SEPARATOR = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public static HttpCookie of(final String line) {
        return new HttpCookie(
                Arrays.stream(line.split(COOKIE_LINE_SEPARATOR))
                        .map(it -> it.split(COOKIE_SEPARATOR))
                        .collect(Collectors.toMap(it -> it[KEY_INDEX], it -> it[VALUE_INDEX]))
        );
    }

    public boolean hasKey(final String key) {
        return cookies.containsKey(key);
    }

    public String getValue(final String key) {
        return cookies.get(key);
    }
}
