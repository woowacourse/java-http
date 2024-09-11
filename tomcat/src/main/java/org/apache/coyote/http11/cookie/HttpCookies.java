package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookies(String cookies) {
        this.cookies = Arrays.stream(cookies.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.split(COOKIE_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1].trim()));
    }

    public boolean hasJSESSIONID() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJSESSIONID() {
        return cookies.get(JSESSIONID);
    }

    public Map<String, String> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }
}
