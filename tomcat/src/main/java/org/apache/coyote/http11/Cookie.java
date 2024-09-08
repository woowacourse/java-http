package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookie {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_SEPARATOR = "=";

    private static final int VALID_COOKIE_PAIR_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    public Cookie(String cookieHeader) {
        this.cookies = Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .map(cookies -> cookies.split(COOKIE_SEPARATOR))
                .filter(cookie -> cookie.length == VALID_COOKIE_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        cookie -> cookie[KEY_INDEX],
                        cookie -> cookie[VALUE_INDEX])
                );
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public String toCookieHeader() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + COOKIE_SEPARATOR + entry.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }

    public Optional<String> getJSessionId() {
        return Optional.ofNullable(cookies.get(JSESSIONID));
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
