package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        final String[] cookiePairs = cookieHeader.split(";");
        for (final String cookiePair : cookiePairs) {
            final String[] nameAndValue = cookiePair.split("=", 2);
            if (nameAndValue.length != 2) {
                continue;
            }

            final String name = nameAndValue[0].trim();
            final String value = nameAndValue[1].trim();

            cookies.put(name, value);
        }
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
