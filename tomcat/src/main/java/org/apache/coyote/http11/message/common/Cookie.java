package org.apache.coyote.http11.message.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.ToString;

@ToString
public class Cookie {

    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_SEPARATOR_REGIX = "; ?";
    private static final String JSESSIONID = "JSESSIONID";

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;


    private Map<String, String> cookie;

    private Cookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static Cookie of(final String key, final String value) {
        LinkedHashMap<String, String> cookie = new LinkedHashMap<>(Map.of(key, value));
        return new Cookie(cookie);
    }

    public static Cookie sessionId(final String sessionId) {
        LinkedHashMap<String, String> cookie = new LinkedHashMap<>(Map.of(JSESSIONID, sessionId));
        return new Cookie(cookie);
    }

    public static Cookie parse(final String header) {
        Map<String, String> cookie = new LinkedHashMap<>();

        for (String singleCookie : header.split(COOKIE_SEPARATOR_REGIX)) {
            List<String> splitCookie = Arrays.asList(singleCookie.split(KEY_VALUE_SEPARATOR));
            cookie.put(splitCookie.get(KEY_INDEX), splitCookie.get(VALUE_INDEX));
        }

        return new Cookie(cookie);
    }

    public String getHeaderValue() {
        return cookie.entrySet()
                .stream()
                .map(it -> it.getKey() + KEY_VALUE_SEPARATOR + it.getValue())
                .collect(Collectors.joining(COOKIE_SEPARATOR));
    }

    public Optional<String> getCookie(final String key) {
        String value = cookie.get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        return Optional.of(value);
    }
}
