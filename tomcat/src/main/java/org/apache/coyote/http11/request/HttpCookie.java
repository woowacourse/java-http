package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> httpCookie;

    public HttpCookie() {
        this(new HashMap<>());
    }

    private HttpCookie(final Map<String, String> httpCookie) {
        this.httpCookie = httpCookie;
    }

    public static HttpCookie from(final String cookieHeader) {
        final Map<String, String> httpCookie = new HashMap<>();

        if (cookieHeader == null || cookieHeader.equals("")) {
            return new HttpCookie(httpCookie);
        }

        final String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            final String[] cookieNameAndValue = cookie.trim().split("=");
            final String cookieName = cookieNameAndValue[0].toLowerCase();
            final String cookieValue = cookieNameAndValue[1].toLowerCase();
            httpCookie.put(cookieName, cookieValue);
        }
        return new HttpCookie(httpCookie);
    }

    public boolean existCookie(final String name) {
        return httpCookie.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase(name));
    }

    public String parseSetCookieHeader(final String name) {
        final String value = httpCookie.get(name);
        return String.format("%s=%s; ", name, value);
    }

    public void addCookie(final String key, final String value) {
        httpCookie.put(key.toLowerCase(), value);
    }

    public String findCookie(final String key) {
        return httpCookie.getOrDefault(key.toLowerCase(), "");
    }
}
