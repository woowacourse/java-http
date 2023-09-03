package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cookie {

    private final Map<String, String> cookies;

    private Cookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(final String cookieString) {
        Map<String, String> cookies = new HashMap<>();
        if (Objects.isNull(cookieString) || cookieString.isEmpty()) {
            return Cookie.empty();
        }
        final String[] splitCookies = cookieString.split("; ");
        for (final String splitCookie : splitCookies) {
            final String[] cookieKeyValue = splitCookie.split("=");
            cookies.put(cookieKeyValue[0], cookieKeyValue[1]);
        }
        return new Cookie(cookies);
    }

    private static Cookie empty() {
        return new Cookie(new HashMap<>());
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public boolean hasKey(final String cookie) {
        return this.cookies.containsKey(cookie);
    }

    public String getCookie(final String cookie) {
        return cookies.get(cookie);
    }
}
