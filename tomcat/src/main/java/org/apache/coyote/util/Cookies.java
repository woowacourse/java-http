package org.apache.coyote.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private final Map<String, Cookie> cookies;

    public Cookies(final Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Cookies createFromRawValues(final String cookieValues) {
        if (cookieValues == null || cookieValues.isBlank()) {
            return new Cookies(Collections.emptyMap());
        }

        Map<String, Cookie> cookies = new HashMap<>();
        String[] cookiePairs = cookieValues.split(";");
        for (String cookiePair : cookiePairs) {
            String[] keyValue = cookiePair.split("=", 2);
            String name = keyValue[0].trim();
            String value = keyValue[1].trim();
            cookies.put(name, new Cookie(name, value));
        }
        return new Cookies(cookies);
    }

    public Cookie getCookie(final String cookieName) {
        return cookies.get(cookieName);
    }
}
