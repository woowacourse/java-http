package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.exception.NotFoundCookieException;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class HttpCookies {

    public static final HttpCookies EMPTY = new HttpCookies(List.of());
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = ";";

    private final List<HttpCookie> cookies;

    private HttpCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies parse(String cookieHeaders) {
        if (cookieHeaders.isEmpty()) {
            return EMPTY;
        }
        return Arrays.stream(cookieHeaders.split(COOKIE_SEPARATOR))
                .map(String::trim)
                .map(cookieHeader -> cookieHeader.split(COOKIE_KEY_VALUE_SEPARATOR))
                .map(cookie -> new HttpCookie(cookie[KEY_INDEX], cookie[VALUE_INDEX]))
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
                .anyMatch(cookie -> cookie.getKey().equalsIgnoreCase(Constant.JSESSIONID));
    }
}
