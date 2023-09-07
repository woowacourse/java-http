package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.exception.NotFoundCookieException;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class HttpCookies {

    public static final HttpCookies EMPTY = new HttpCookies(List.of());

    private final List<HttpCookie> cookies;

    private HttpCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies parse(String cookieHeader) {
        if (cookieHeader.isEmpty()) {
            return EMPTY;
        }
        return Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(it -> it.split("="))
                .map(it -> new HttpCookie(it[0], it[1]))
                .collect(collectingAndThen(toList(), HttpCookies::new));
    }

    public String getCookieValue(String key) {
        return cookies.stream()
                .filter(cookie -> cookie.getKey().equalsIgnoreCase(key))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow(NotFoundCookieException::new);
    }

    public boolean existsSession() {
        return cookies.stream()
                .anyMatch(cookie -> cookie.getKey().equalsIgnoreCase("JSESSIONID"));
    }
}
