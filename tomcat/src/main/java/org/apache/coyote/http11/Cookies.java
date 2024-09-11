package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_SEPARATOR = "=";

    private static final int VALID_COOKIE_PAIR_LENGTH = 2;
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    public Cookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Cookies(String cookieHeader) {
        this.cookies = Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .map(cookies -> cookies.split(COOKIE_SEPARATOR))
                .filter(cookie -> cookie.length == VALID_COOKIE_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        cookie -> cookie[NAME_INDEX],
                        cookie -> cookie[VALUE_INDEX])
                );
    }

    public static Cookies ofJSessionId(String id) {
        return new Cookies(Map.of(JSESSIONID, id));
    }

    public static Cookies empty() {
        return new Cookies(Collections.emptyMap());
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
