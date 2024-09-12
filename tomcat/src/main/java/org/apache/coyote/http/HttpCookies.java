package org.apache.coyote.http;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookies implements HttpComponent {

    private static final String SEPARATOR = ";";

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, HttpCookie> cookies;

    public HttpCookies() {
        cookies = new ConcurrentHashMap<>();
    }

    public HttpCookies(final String cookieHeaderLine) {
        this();
        if (cookieHeaderLine == null || cookieHeaderLine.isEmpty()) {
            return;
        }
        String[] httpCookies = cookieHeaderLine.split(SEPARATOR);
        for (String cookie : httpCookies) {
            String[] cookieParts = cookie.split(KEY_VALUE_SEPARATOR);
            String name = cookieParts[NAME_INDEX].trim();
            String value = cookieParts[VALUE_INDEX].trim();
            cookies.put(name, new HttpCookie(name, value));
        }
    }

    public void add(final HttpCookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public HttpCookie get(final String name) {
        return cookies.get(name);
    }

    @Override
    public String asString() {
        StringJoiner result = new StringJoiner(SEPARATOR + SPACE);
        for (HttpCookie cookie : cookies.values()) {
            result.add(cookie.asString());
        }
        return result.toString();
    }
}
