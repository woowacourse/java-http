package org.apache.coyote.http11.common.header;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_STRING_SEPARATOR = "; ";
    private static final String COOKIE_VALUE_SEPARATOR = "=";
    private static final int COOKIE_NAME_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;


    private final Map<String, String> cookie;

    public HttpCookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie from(final String cookies) {
        final Map<String, String> httpCookie = new HashMap<>();

        final StringTokenizer stringTokenizer = new StringTokenizer(cookies, COOKIE_STRING_SEPARATOR);
        while (stringTokenizer.hasMoreTokens()) {
            final String cookie = stringTokenizer.nextToken();
            final String[] split = cookie.split(COOKIE_VALUE_SEPARATOR);
            httpCookie.put(split[COOKIE_NAME_INDEX], split[COOKIE_VALUE_INDEX]);
        }

        return new HttpCookie(httpCookie);
    }

    public boolean isEmpty() {
        return cookie.isEmpty();
    }

    public String search(final String cookieKey) {
        return cookie.get(cookieKey);
    }

    public String convertToString() {
        return cookie.keySet().stream()
                     .map(key -> key + COOKIE_VALUE_SEPARATOR + cookie.get(key))
                     .collect(Collectors.joining(COOKIE_STRING_SEPARATOR));
    }

    public Map<String, String> getCookie() {
        return cookie;
    }
}
