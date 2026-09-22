package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpCookie {

    private final Map<String, String> cookieMap;

    private HttpCookie(final Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public static HttpCookie from(final String rawCookies) {
        if (rawCookies == null || rawCookies.isEmpty()) {
            return new HttpCookie(Map.of());
        }
        final Map<String, String> cookieMap = new LinkedHashMap<>();
        Arrays.stream(rawCookies.trim().split("; "))
            .map(HttpCookie::parseCookiePair)
            .forEach(cookiePair ->
                cookieMap.put(cookiePair.getKey(), cookiePair.getValue()));

        return new HttpCookie(cookieMap);
    }

    private static Entry<String, String> parseCookiePair(String cookieToken) {
        final int firstEqualSignIndex = cookieToken.indexOf("=");

        return Map.entry(
            cookieToken.substring(0, firstEqualSignIndex),
            cookieToken.substring(firstEqualSignIndex + 1));
    }

    public String getValue(final String key) {
        return cookieMap.get(key);
    }

}
