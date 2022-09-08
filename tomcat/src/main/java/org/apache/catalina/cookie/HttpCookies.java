package org.apache.catalina.cookie;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeader;

public class HttpCookies {

    private static final String COOKIE_DELIMITER = "; ";

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

    public HttpCookie getCookie(String name) {
        return values.get(name);
    }

    public void setCookie(String name, HttpCookie value) {
        values.put(name, value);
    }

    public boolean containsCookies() {
        return !values.isEmpty();
    }

    public List<String> toSetHeaderFormats() {
        return values.values()
                .stream()
                .map(HttpCookie::toHeaderFormat)
                .map(HttpHeader.SET_COOKIE::toLineFormat)
                .collect(Collectors.toList());
    }
}
