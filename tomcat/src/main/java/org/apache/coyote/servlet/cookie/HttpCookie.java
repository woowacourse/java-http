package org.apache.coyote.servlet.cookie;

import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class HttpCookie {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_FORMAT_ELEMENT_COUNT = 2;

    private final String name;
    private final String value;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpCookie of(String cookie) {
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
