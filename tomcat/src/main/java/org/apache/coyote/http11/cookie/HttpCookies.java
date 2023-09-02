package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.exception.NotFoundCookieException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpCookies {

    private final List<HttpCookie> cookies;

    private HttpCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies parse(String cookieHeader) {
        List<HttpCookie> collect = Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(it -> it.split("="))
                .map(it -> new HttpCookie(it[0], it[1]))
                .collect(Collectors.toList());
        return new HttpCookies(collect);
    }

    public String getCookieValue(String key) {
        return cookies.stream()
                .filter(it -> it.getKey().equalsIgnoreCase(key))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow(NotFoundCookieException::new);
    }

    public boolean existsSession() {
        return cookies.stream()
                .anyMatch(it -> it.getKey().equalsIgnoreCase("JSESSIONID"));
    }
}
