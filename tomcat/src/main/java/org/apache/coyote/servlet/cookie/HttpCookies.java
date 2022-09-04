package org.apache.coyote.servlet.cookie;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String SET_COOKIE_HEADER_FORMAT = "Set-Cookie: %s ";

    private final Map<String, HttpCookie> values;

    public HttpCookies(Map<String, HttpCookie> values) {
        this.values = values;
    }

    public static HttpCookies ofRequestHeader(String headerLine) {
        Map<String, HttpCookie> cookies = Arrays.stream(headerLine.split(COOKIE_DELIMITER))
                .map(HttpCookie::of)
                .collect(Collectors.toMap(HttpCookie::getName, cookie -> cookie));
        return new HttpCookies(cookies);
    }

    public boolean containsCookies() {
        return !values.isEmpty();
    }

    public String toSetHeaderFormat() {
        final var cookies = values.values()
                .stream()
                .map(HttpCookie::toHeaderFormat)
                .collect(Collectors.toList());
        return String.format(SET_COOKIE_HEADER_FORMAT, cookies);
    }
}
