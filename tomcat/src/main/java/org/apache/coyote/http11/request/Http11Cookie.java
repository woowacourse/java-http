package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Http11Cookie {

    private final Map<String, String> cookies = new HashMap<>();

    public Http11Cookie(final String cookieHeader) {
        if (validateCookieHeader(cookieHeader)) {
            return;
        }

        extractCookieHeader(cookieHeader);
    }

    private boolean validateCookieHeader(final String cookieHeader) {
        return cookieHeader == null || cookieHeader.isEmpty();
    }

    private void extractCookieHeader(final String cookieHeader) {
        Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(s -> s.split("=", 2))
                .filter(kv -> kv.length == 2)
                .forEach(kv -> cookies.put(kv[0], kv[1]));
    }

    public String get(final String cookieKey) {
        return cookies.get(cookieKey);
    }
}
