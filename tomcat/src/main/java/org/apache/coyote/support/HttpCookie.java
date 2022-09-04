package org.apache.coyote.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String COOKIE_REQUEST_HEADER = "Cookie";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_FORMAT_ELEMENT_COUNT = 2;

    private final String name;
    private final String value;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static List<HttpCookie> extractCookies(String headerLine) {
        return Arrays.stream(headerLine.split(COOKIE_DELIMITER))
                .map(HttpCookie::extractCookie)
                .collect(Collectors.toList());
    }

    private static HttpCookie extractCookie(String cookie) {
        final var keyValues = cookie.split(KEY_VALUE_DELIMITER);
        if (keyValues.length != COOKIE_FORMAT_ELEMENT_COUNT) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        return new HttpCookie(keyValues[0], keyValues[1]);
    }

    public String toHeaderFormat() {
        return name + KEY_VALUE_DELIMITER + value;
    }

    public String getName() {
        return name;
    }
}
