package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private static final String COOKIE = "Cookie";
    private static final String COOKIE_DELIMETER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMETER = "=";

    private final Map<String, String> cookies = new HashMap<>();

    public static HttpCookies from(HttpHeaders httpHeaders) {
        HttpCookies httpCookies = new HttpCookies();

        String headerValue = httpHeaders.getHeaderValue(COOKIE);
        if (headerValue == null) {
            return httpCookies;
        }
        
        String[] headerCookies = headerValue.split(COOKIE_DELIMETER);
        for (String cookie : headerCookies) {
            String[] cookieParts = cookie.split(COOKIE_KEY_VALUE_DELIMETER);
            String key = cookieParts[0];
            String value = cookieParts[1];

            httpCookies.putCookie(key, value);
        }

        return httpCookies;
    }

    public String getCookieValue(String cookieName) {
        return cookies.get(cookieName);
    }

    public void putCookie(String name, String value) {
        cookies.put(name, value);
    }
}
