package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.exception.InvalidHttpRequestFormatException;

public class HttpCookie {

    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int COOKIE_FORMAT_SIZE = 2;

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parseCookie(final String cookieHeaderString) {
        Map<String, String> cookies = new HashMap<>();
        String[] rawCookies = cookieHeaderString.split(COOKIES_DELIMITER);
        for (String rawCookie : rawCookies) {
            String[] keyAndValue = rawCookie.split(COOKIE_DELIMITER);
            validateCookieFormat(keyAndValue);
            cookies.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new HttpCookie(cookies);
    }

    private static void validateCookieFormat(final String[] keyAndValue) {
        if (keyAndValue.length != COOKIE_FORMAT_SIZE) {
            throw new InvalidHttpRequestFormatException();
        }
    }

    public String get(final String key) {
        return cookies.get(key);
    }
}
