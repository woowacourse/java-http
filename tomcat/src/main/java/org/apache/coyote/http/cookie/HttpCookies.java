package org.apache.coyote.http.cookie;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private static final String KEY = "JSESSIONID";
    private static final String COOKIE_DELIMITER = ";";
    private static final String KEY_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_PAIR = 2;

    private final Map<String, HttpCookie> values;

    private HttpCookies(Map<String, HttpCookie> values) {
        this.values = values;
    }

    public static HttpCookies from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return new HttpCookies(new HashMap<>());
        }

        return new HttpCookies(Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.trim().split(KEY_DELIMITER, KEY_VALUE_PAIR))
                .collect(Collectors.toMap(
                        result -> result[KEY_INDEX],
                        result -> new HttpCookie(result[KEY_INDEX], result[VALUE_INDEX])
                ))
        );
    }

    public void addCookie(HttpCookie httpCookie) {
        values.put(httpCookie.getName(), httpCookie);
    }

    public boolean hasJsessionId() {
        return values.containsKey(KEY);
    }

    public String getJsessionId() {
        return values.get(KEY).getValue();
    }

    public Map<String, HttpCookie> getCookies() {
        return Collections.unmodifiableMap(values);
    }
}
