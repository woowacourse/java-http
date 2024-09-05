package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    public final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public HttpCookie(String cookie) {
        this.cookie = parseCookie(cookie);
    }

    public static Map<String, String> parseCookie(String cookie) {
        String[] cookiePairs = cookie.split("; ");

        Map<String, String> cookies = new HashMap<>();
        Arrays.stream(cookiePairs)
                .forEach(pair -> {
                    String[] keyAndValue = pair.split("=");
                    cookies.put(keyAndValue[0], keyAndValue[1]);
                });

        return cookies;
    }
}
