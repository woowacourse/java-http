package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Http11Cookie {
    private final Map<String, String> cookies;

    public static Http11Cookie create(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        final String[] pairs = cookieHeader.split("; ");
        for (final String pair : pairs) {
            final String[] keyAndValue = pair.split("=");
            if (keyAndValue.length != 2) {
                throw new IllegalArgumentException(String.format("Wrong Http Request Cookies: %s", String.join("", keyAndValue)));
            }
            cookies.put(keyAndValue[0], keyAndValue[1]);
        }

        return new Http11Cookie(cookies);
    }

    private Http11Cookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean hasCookie(final String cookieKeyName) {
        return cookies.containsKey(cookieKeyName);
    }
}
