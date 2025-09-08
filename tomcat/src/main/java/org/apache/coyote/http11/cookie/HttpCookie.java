package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    public static HttpCookie from(final String rawCookie) {
        if (rawCookie.isBlank()) {
            return new HttpCookie(new HashMap<>());
        }
        final String[] cookiePairs = rawCookie.split(COOKIE_SEPARATOR);
        final Map<String, String> cookies = Arrays.stream(cookiePairs)
                .map(param -> param.split(KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1],
                        (oldValue, newValue) -> newValue
                ));

        return new HttpCookie(cookies);
    }

    public String get(final String key) {
        return cookies.get(key);
    }

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }
}
