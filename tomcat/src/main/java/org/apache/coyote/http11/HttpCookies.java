package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private static final String COOKIE_DELIMITER = ";";
    private static final String EQUALS = "=";
    private static final int SPLIT_LIMIT = 2;
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, HttpCookie> cookies = new HashMap<>();

    public HttpCookies(String cookieLine) {
        if (cookieLine == null) {
            return;
        }
        Arrays.stream(cookieLine.split(COOKIE_DELIMITER))
                .map(token -> token.split(EQUALS, SPLIT_LIMIT))
                .map(cookieToken -> new HttpCookie(cookieToken[NAME_INDEX].trim(), cookieToken[VALUE_INDEX].trim()))
                .forEach(cookie -> cookies.put(cookie.getName(), cookie));
    }

    public void add(HttpCookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public HttpCookie get(String name) {
        return cookies.get(name);
    }

    public Map<String, HttpCookie> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }
}
