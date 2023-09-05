package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookies {

    private static final String SESSION_COOKIE = "JSESSIONID";
    private static final String COOKIE_SPLITER = ";";
    private static final String COOKIE_VALUE_SPLITER = "=";

    private final Map<String, String> cookieValues;

    public Cookies(String cookies) {
        this.cookieValues = Stream.of(cookies.split(COOKIE_SPLITER))
            .collect(Collectors.toMap(
                this::extractKey,
                this::extractValue
            ));
    }

    private String extractKey(String value) {
        String[] split = value.split(COOKIE_VALUE_SPLITER);
        return split[0].trim();
    }

    private String extractValue(String value) {
        String[] split = value.split(COOKIE_VALUE_SPLITER);
        return split[1].trim();
    }

    public Optional<String> getCookieOf(String cookieName) {
        if (!cookieValues.containsKey(cookieName)) {
            return Optional.empty();
        }
        return Optional.of(cookieValues.get(cookieName));
    }

    public Optional<String> getSessionCookie() {
        if (!cookieValues.containsKey(SESSION_COOKIE)) {
            return Optional.empty();
        }
        return Optional.of(cookieValues.get(SESSION_COOKIE));
    }
}
