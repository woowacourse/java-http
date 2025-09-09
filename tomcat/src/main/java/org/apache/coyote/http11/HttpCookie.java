package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final String cookieHeader) {
        this.cookies = new HashMap<>();
        parseCookies(cookieHeader);
    }

    private void parseCookies(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return;
        }

        final String[] cookiePairs = cookieHeader.split(";");
        for (final String cookiePair : cookiePairs) {
            final String trimmedPair = cookiePair.trim();
            final int equalIndex = trimmedPair.indexOf('=');

            if (equalIndex > 0 && equalIndex < trimmedPair.length() - 1) {
                final String name = trimmedPair.substring(0, equalIndex).trim();
                final String value = trimmedPair.substring(equalIndex + 1).trim();
                cookies.put(name, value);
            }
        }
    }

    public String getCookieValue(final String name) {
        return cookies.get(name);
    }

    public String getJSessionId() {
        return getCookieValue("JSESSIONID");
    }
}
