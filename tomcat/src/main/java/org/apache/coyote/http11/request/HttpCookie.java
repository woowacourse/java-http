package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String INPUT_SPLIT_REGEX = "; ";
    private static final String COOKIE_SPLIT_REGEX = "=";

    private final Map<String, String> value;

    private HttpCookie(Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie of(String input) {
        Map<String, String> value = Arrays.stream(input.split(INPUT_SPLIT_REGEX))
            .map(cookie -> cookie.split(COOKIE_SPLIT_REGEX))
            .collect(Collectors.toMap(query -> query[0], query -> query[1]));

        return new HttpCookie(value);
    }

    public String getOrDefault(String key) {
        return value.getOrDefault(key, "");
    }
}
