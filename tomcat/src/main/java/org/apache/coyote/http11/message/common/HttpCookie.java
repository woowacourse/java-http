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
public class HttpCookie {

    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_SEPARATOR_REGEX = "; ?";
    private static final String JSESSIONID = "JSESSIONID";

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(final String key, final String value) {
        LinkedHashMap<String, String> cookie = new LinkedHashMap<>(Map.of(key, value));
        return new HttpCookie(cookie);
    }

    public static HttpCookie sessionId(final String sessionId) {
        LinkedHashMap<String, String> cookie = new LinkedHashMap<>(Map.of(JSESSIONID, sessionId));
        return new HttpCookie(cookie);
    }

    public static HttpCookie parse(final String header) {
        Map<String, String> cookies = new LinkedHashMap<>();

        for (String singleCookie : header.split(COOKIE_SEPARATOR_REGEX)) {
            List<String> splitCookie = Arrays.asList(singleCookie.split(KEY_VALUE_SEPARATOR));
            cookies.put(splitCookie.get(KEY_INDEX), splitCookie.get(VALUE_INDEX));
        }

        return new HttpCookie(cookies);
    }

    public String generateHeaderValue() {
        return values.entrySet()
                .stream()
                .map(it -> it.getKey() + KEY_VALUE_SEPARATOR + it.getValue())
                .collect(Collectors.joining(COOKIE_SEPARATOR));
    }

    public Optional<String> getValues(final String key) {
        String value = values.get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        return Optional.of(value);
    }
}
