package org.apache.catalina.cookie;

import org.apache.catalina.session.Session;
import org.apache.coyote.support.HttpException;

public class HttpCookie {

    private static final int MAX_AGE_UNSPECIFIED = -1;
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_FORMAT_ELEMENT_COUNT = 2;
    private static final String SET_MAX_AGE_FORMAT = "; Max-Age=%d";

    private final String name;
    private final String value;
    private final int maxAge;

    private HttpCookie(String name, String value, int maxAge) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
    }

    private HttpCookie(String name, String value) {
        this(name, value, MAX_AGE_UNSPECIFIED);
    }

    public static HttpCookie of(String cookie) {
        final var keyValues = cookie.split(KEY_VALUE_DELIMITER);
        if (keyValues.length != COOKIE_FORMAT_ELEMENT_COUNT) {
            throw HttpException.ofBadRequest();
        }
        return new HttpCookie(keyValues[0], keyValues[1]);
    }

    public static HttpCookie ofSession(Session session) {
        return new HttpCookie(Session.JSESSIONID, session.getId(), Session.VALIDITY_IN_SECONDS);
    }

    public String toHeaderFormat() {
        String cookie = name + KEY_VALUE_DELIMITER + value;
        if (maxAge != MAX_AGE_UNSPECIFIED) {
            cookie = cookie + String.format(SET_MAX_AGE_FORMAT, maxAge);
        }
        return cookie;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
