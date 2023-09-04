package org.apache.coyote.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if (Objects.isNull(cookieValues) || cookieValues.isBlank()) {
            return empty();
        }

        return new Cookies(collectCookieMapping(cookieValues));
    }

    private static Map<String, String> collectCookieMapping(final String cookieValues) {
        return Arrays.stream(cookieValues.split(COOKIE_HEADER_DELIMITER))
                .map(cookieEntry -> cookieEntry.split(COOKIE_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        cookieEntry -> cookieEntry[COOKIE_KEY_INDEX].trim(),
                        cookieEntry -> cookieEntry[COOKIE_VALUE_INDEX].trim()
                ));
    }

    public static Cookies ofJSessionId(final String sessionId) {
        return new Cookies(Map.of("JSESSIONID", sessionId));
    }

    public static Cookies empty() {
        return new Cookies(new HashMap<>());
    }

    public void addCookie(final Cookies other) {
        cookie.putAll(other.cookie);
    }

    public List<String> cookieNames() {
        return new ArrayList<>(cookie.keySet());
    }

    public String getCookieValue(final String cookieName) {
        return cookie.getOrDefault(cookieName, null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Cookies cookies = (Cookies) o;
        return Objects.equals(cookie, cookies.cookie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cookie);
    }

    @Override
    public String toString() {
        return "Cookies{" +
               "cookie=" + cookie +
               '}';
    }
}
