package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestCookies {

    private final Map<String, String> cookies = new HashMap<>();

    public void parseCookies(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return;
        }
        final String[] cookiePairs = cookieHeader.split(";");
        for (String cookiePair : cookiePairs) {
            final String[] parts = cookiePair.split("=", 2);
            if (parts.length == 2) {
                final String cookieName = parts[0].trim();
                final String cookieValue = parts[1].trim();
                addCookie(cookieName, cookieValue);
            }
        }
    }

    public void addCookie(final String cookieName, final String cookieValue) {
        cookies.put(cookieName, cookieValue);
    }

    public String getCookie(final String cookieName) {
        return cookies.get(cookieName);
    }
}
