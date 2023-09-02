package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestCookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_DELIMITER = "=";


    private final Map<String, String> cookies;

    public HttpRequestCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpRequestCookie from(final String cookieHeader) {
        if (cookieHeader == null) {
            return new HttpRequestCookie(Map.of());
        }

        final Map<String, String> cookieKeyValues = Arrays.stream(cookieHeader.split(COOKIE_SEPARATOR))
                .map(param -> param.split(COOKIE_DELIMITER))
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        return new HttpRequestCookie(cookieKeyValues);
    }

    public String getCookieValue(final String cookieKey) {
        return cookies.get(cookieKey);
    }
}
