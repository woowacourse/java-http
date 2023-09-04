package org.apache.coyote.http11.auth;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequestHeader;

public class HttpCookie {

    private static final String COOKIE_HEADER_KEY = "Cookie";
    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String SEPARATOR = "; ";
    private static final String DELIMITER = "=";

    private final Map<String, String> httpCookies;

    private HttpCookie(final Map<String, String> httpCookies) {
        this.httpCookies = httpCookies;
    }

    public static HttpCookie from(final HttpRequestHeader httpRequestHeader) {
        final String cookie = httpRequestHeader.get(COOKIE_HEADER_KEY);
        if ("none".equals(cookie)) {
            return new HttpCookie(Collections.emptyMap());
        }

        return Arrays.stream(cookie.split(SEPARATOR))
                .map(perCookie -> perCookie.split(DELIMITER))
                .collect(collectingAndThen(toUnmodifiableMap(
                        perCookie -> perCookie[KEY_INDEX],
                        perCookie -> perCookie[VALUE_INDEX]
                ), HttpCookie::new));
    }

    public boolean noneJSessionId() {
        return !this.httpCookies.containsKey(JSESSIONID_KEY);
    }

    public String getValue(final String key) {
        return null;
    }
}
