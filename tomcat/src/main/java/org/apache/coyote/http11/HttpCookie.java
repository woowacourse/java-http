package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookieMap;

    private HttpCookie() {
        this.cookieMap = Map.of();
    }

    private HttpCookie(final Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public static HttpCookie from(final String rawCookies) {
        if (rawCookies == null || rawCookies.isEmpty()) {
            return new HttpCookie();
        }
        final Map<String, String> cookieMap = new LinkedHashMap<>();
        Arrays.stream(rawCookies.trim().split("; "))
            .map(cookieToken -> cookieToken.split("="))
            .forEach(cookiePair -> cookieMap.put(cookiePair[0], cookiePair[1]));

        return new HttpCookie(cookieMap);
    }

    public boolean containsJSessionId() {
        return cookieMap.containsKey("JSESSIONID");
    }

}
