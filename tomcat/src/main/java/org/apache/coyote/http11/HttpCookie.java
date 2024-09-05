package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> header;

    public HttpCookie() {
        this.header = new HashMap<>();
    }

    public static HttpCookie parse(final String str) {
        final var httpCookie = new HttpCookie();
        final var cookies = str.split(";");
        for (final var cookie : cookies) {
            final var key = cookie.split("=")[0].strip();
            final var value = cookie.split("=")[1].strip();
            httpCookie.header.put(key, value);
        }

        return httpCookie;
    }

    public Map<String, String> getHeader() {
        return Collections.unmodifiableMap(header);
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
               "header=" + header +
               '}';
    }
}
