package org.apache.coyote.http11;

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

    public String get(final String cookieKey) {
        return cookies.get(cookieKey);
    }

    private boolean validateCookieHeader(final String cookieHeader) {
        return cookieHeader == null || cookieHeader.isEmpty();
    }
    // yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46

    private void extractCookieHeader(final String cookieHeader) {
        Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(s -> s.split("=", 2))
                .filter(kv -> kv.length == 2)
                .forEach(kv -> cookies.put(kv[0], kv[1]));
    }
}
