package org.apache.coyote.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private static final String COOKIE_HEADER_DELIMITER = ";";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> cookie;

    private Cookies(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static Cookies from(final String cookieValues) {
        final Map<String, String> mapping = Arrays.stream(cookieValues.split(COOKIE_HEADER_DELIMITER))
                .map(cookieEntry -> cookieEntry.split(COOKIE_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        cookieEntry -> cookieEntry[COOKIE_KEY_INDEX].trim(),
                        cookieEntry -> cookieEntry[COOKIE_VALUE_INDEX].trim()
                ));

        return new Cookies(mapping);
    }

    public static Cookies ofJSessionId(final String sessionId) {
        return new Cookies(Map.of("JSESSIONID", sessionId));
    }

    public List<String> names() {
        return new ArrayList<>(cookie.keySet());
    }

    public String getCookieValue(final String name) {
        return cookie.getOrDefault(name, null);
    }

    @Override
    public String toString() {
        return "Cookies{" +
               "cookie=" + cookie +
               '}';
    }
}
