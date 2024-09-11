package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;

public class HttpCookie {
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_TOKEN_DELIMITER = "=";
    public static final int COOKIE_NAME_INDEX = 0;
    public static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private HttpCookie() {
        cookies = new HashMap<>();
    }

    private HttpCookie(final String values) {
        this.cookies = Arrays.stream(values.split(COOKIE_DELIMITER))
                .map(token -> token.split(COOKIE_TOKEN_DELIMITER))
                .collect(Collectors.toMap(value -> value[COOKIE_NAME_INDEX], value -> value[COOKIE_VALUE_INDEX]));
    }

    public static HttpCookie from(final Session session) {
        return new HttpCookie(SESSION_COOKIE_NAME + COOKIE_TOKEN_DELIMITER + session.getId());
    }

    public static HttpCookie from(final String cookies) {
        if ("".equals(cookies)) {
            return new HttpCookie();
        }
        return new HttpCookie(cookies);
    }

    public boolean containsSession() {
        return cookies.containsKey(SESSION_COOKIE_NAME);
    }

    public String parseCookiesToQueryString() {
        return cookies.keySet()
                .stream()
                .map(name -> name + COOKIE_TOKEN_DELIMITER + cookies.get(name))
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }

    public String getSessionCookie() {
        return cookies.get(SESSION_COOKIE_NAME);
    }
}
