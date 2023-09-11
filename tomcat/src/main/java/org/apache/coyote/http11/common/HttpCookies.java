package org.apache.coyote.http11.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.BadRequestException;

public class HttpCookies {

    private static final String COOKIE_DELIMITER = ", ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_KEY_INDEX = 0;

    private final Map<String, Cookie> cookies;

    private HttpCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(String line) {
        Map<String, Cookie> cookies = new HashMap<>();

        String[] elements = line.split(COOKIE_DELIMITER);

        for (String element : elements) {
            String cookieKey = element.split(KEY_VALUE_DELIMITER)[COOKIE_KEY_INDEX];
            Cookie cookie = Cookie.from(element);

            cookies.put(cookieKey, cookie);
        }

        return new HttpCookies(cookies);
    }

    public static HttpCookies createEmptyCookies() {
        return new HttpCookies(new LinkedHashMap<>());
    }

    public void addCookie(String key, Cookie cookie) {
        cookies.put(key, cookie);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public Map<String, Cookie> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }

    public Cookie get(String key) {
        validateKey(key);

        return cookies.get(key);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new BadRequestException("Cookie key is Null");
        }
    }

}
