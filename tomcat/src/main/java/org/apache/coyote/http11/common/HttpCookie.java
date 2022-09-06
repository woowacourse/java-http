package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_MAPPER = "=";

    private static final String SESSION = "JSESSIONID";

    private final Map<String, String> cookie;

    private HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public static HttpCookie createByParsing(String input) {
        Map<String, String> httpCookie = new HashMap<>();

        String[] cookies = input.split(COOKIE_DELIMITER);
        for (String cookie : cookies) {
            String[] parsedCookie = cookie.split(COOKIE_MAPPER);
            String cookieKey = parsedCookie[0];
            String cookieValue = parsedCookie[1];

            httpCookie.put(cookieKey, cookieValue);
        }

        return new HttpCookie(httpCookie);
    }

    public static HttpCookie createWithSession(String sessionId) {
        Map<String, String> httpCookie = new HashMap<>();
        httpCookie.put(SESSION, sessionId);

        return new HttpCookie(httpCookie);
    }

    public boolean containsSession() {
        return cookie.containsKey(SESSION);
    }

    public String getSession() {
        return cookie.get(SESSION);
    }

    public String toHeaderString() {
        return cookie.entrySet().stream()
            .map(cookie -> cookie.getKey() + COOKIE_MAPPER + cookie.getValue())
            .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
