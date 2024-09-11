package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String COOKIE_VALUE_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = "; ";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(HttpHeader httpHeader) {
        String rawCookies = httpHeader.getHeader(HttpHeaderName.COOKIE);

        if (rawCookies == null) {
            return new HttpCookie(new HashMap<>());
        }

        Map<String, String> cookies = Arrays.stream(rawCookies.split(COOKIE_DELIMITER))
                .collect(Collectors.toMap(
                        cookie -> cookie.split(COOKIE_VALUE_DELIMITER)[0],
                        cookie -> cookie.split(COOKIE_VALUE_DELIMITER)[1])
                );

        return new HttpCookie(cookies);
    }

    public boolean isContains(String cookieName) {
        return cookies.containsKey(cookieName);
    }

    public String findCookie(String key) {
        return cookies.get(key);
    }
}
