package org.apache.coyote.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class RequestCookies {

    private static final RequestCookies EMPTY = new RequestCookies(Collections.emptyMap());

    private final Map<String, String> cookies;

    private RequestCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static RequestCookies from(String cookies) {
        if (cookies == null) {
            return EMPTY;
        }
        return Arrays.stream(cookies.split("; "))
                .map(cookie -> cookie.split("=", 2))
                .collect(collectingAndThen(toMap(c -> c[0], c -> c[1]), RequestCookies::new));
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
