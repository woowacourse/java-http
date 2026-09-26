package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null) {
            return;
        }
        parse(cookieHeader);
    }

    private void parse(final String cookieHeader) {
        final String[] pairs = cookieHeader.split(";");

        for (final String pair : pairs) {
            final String[] keyValue = pair.trim().split("=", 2);
            cookies.put(keyValue[0], keyValue[1]);
        }
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
