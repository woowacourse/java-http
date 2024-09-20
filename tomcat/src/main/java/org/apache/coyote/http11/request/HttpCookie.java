package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookie {
    public static final HttpCookie EMPTY = new HttpCookie(new HashMap<>());

    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = ";";

    private final Map<String, String> value;

    public HttpCookie(Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie from(String data) {
        try {
            return new HttpCookie(
                    Arrays.stream(data.split(COOKIE_DELIMITER))
                            .map(keyValue -> keyValue.split(COOKIE_KEY_VALUE_DELIMITER))
                            .collect(Collectors.toMap(key -> key[0], value -> value[1]))
            );
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return EMPTY;
        }
    }

    public String getValue(String key) {
        return value.get(key);
    }
}
