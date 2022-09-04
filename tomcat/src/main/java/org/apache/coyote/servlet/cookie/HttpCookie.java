package org.apache.coyote.servlet.cookie;

import java.util.UUID;
import org.apache.coyote.servlet.session.Session;
import org.apache.coyote.support.HttpException;

public class HttpCookie {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_FORMAT_ELEMENT_COUNT = 2;
    private static final String SET_MAX_AGE_FORMAT = "; Max-Age=%d";

    private final String name;
    private final String value;
    private final Integer maxAge;

    private HttpCookie(String name, String value, Integer maxAge) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
    }

    private HttpCookie(String name, String value) {
        this(name, value, null);
    }

    public static HttpCookie of(String cookie) {
        final var keyValues = cookie.split(KEY_VALUE_DELIMITER);
        if (keyValues.length != COOKIE_FORMAT_ELEMENT_COUNT) {
            throw HttpException.ofBadRequest();
        }
        return new HttpCookie(keyValues[0], keyValues[1]);
    }

    public static HttpCookie ofSessionId(UUID sessionId) {
        return new HttpCookie(Session.JSESSIONID, sessionId.toString(), Session.VALIDITY_IN_SECONDS);
    }

    public String toHeaderFormat() {
        String cookie = name + KEY_VALUE_DELIMITER + value;
        if (maxAge != null) {
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
