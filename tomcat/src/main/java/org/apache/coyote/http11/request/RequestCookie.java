package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestCookie {

    public static final String COOKIE_HEADER_KEY = "Cookie";
    public static final String COOKIE_DELIMITER = "; ";
    public static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    public RequestCookie() {
        this.cookies = new HashMap<>();
    }

    public void setCookie(final String attribute) {
        final String[] cookieKeyValue = attribute.split(RequestCookie.KEY_VALUE_DELIMITER);
        setCookie(cookieKeyValue[KEY_INDEX], cookieKeyValue[VALUE_INDEX]);
    }

    public void setCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public String get(final String key) {
        return cookies.get(key);
    }
}
