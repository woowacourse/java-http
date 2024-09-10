package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;

public class HttpCookie {
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_TOKEN_DELIMITER = "=";

    private final Map<String, String> cookies;

    private HttpCookie(final String values) {
        this.cookies = Arrays.stream(values.split(COOKIE_DELIMITER))
                .map(token -> token.split(COOKIE_TOKEN_DELIMITER))
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
    }

    public static HttpCookie from(final Session session) {
        return new HttpCookie(SESSION_COOKIE_NAME + COOKIE_TOKEN_DELIMITER + session.getId());
    }

    public static HttpCookie from(final String cookies) {
        return new HttpCookie(cookies);
    }

    public boolean contains(final String key) {
        return cookies.containsKey(key);
    }

    public String getCookieValue(final String key) {
        return cookies.get(key);
    }

    public String parseCookiesToQueryString() {
        return cookies.keySet()
                .stream()
                .map(name -> name + COOKIE_TOKEN_DELIMITER + cookies.get(name))
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
