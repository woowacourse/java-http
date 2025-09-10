package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestCookies {

    private final Map<String, String> cookies;

    private RequestCookies(final Map<String, String> cookies) {
        this.cookies = Collections.unmodifiableMap(cookies);
    }

    public static RequestCookies from(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new RequestCookies(Collections.emptyMap());
        }

        final Map<String, String> cookies = new HashMap<>();
        final String[] cookiePairs = cookieHeader.split(";");
        for (final String pair : cookiePairs) {
            final String[] keyValue = pair.trim().split("=");
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return new RequestCookies(cookies);
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }
}
