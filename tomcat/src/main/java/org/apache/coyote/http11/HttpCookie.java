package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String COOKIE = "Cookie";
    public static final String JSESSIONID = "JSESSIONID";

    private static final String COOKIE_TYPE_DELIMITER = ";";

    private final Map<String, String> cookies;

    public HttpCookie(final String cookieHeader) {
        this.cookies = parseCookie(cookieHeader);
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
        cookies.put(JSESSIONID, generateJSessionId());
    }

    public static HttpCookie fromJSessionId(final String id) {
        return new HttpCookie(JSESSIONID + "=" + id);
    }

    private String generateJSessionId() {
        return UUID.randomUUID().toString();
    }

    private Map<String, String> parseCookie(final String cookieHeader) {

        final List<String[]> result = Arrays.stream(cookieHeader.split(COOKIE_TYPE_DELIMITER))
            .map(it -> it.trim().split("="))
            .collect(Collectors.toList());

        return result.stream()
            .collect(Collectors
                .toMap(cookie -> cookie[0], cookie -> cookie[1], (a, b) -> b));
    }

    public String getCookieValue(final String cookieName) {
        return cookies.get(cookieName);
    }

    public String getJSessionCookieHeader() {
        return JSESSIONID + "=" + cookies.get(JSESSIONID);
    }
}
