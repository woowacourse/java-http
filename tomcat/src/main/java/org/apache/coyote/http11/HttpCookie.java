package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String rawCookies) {
        if (rawCookies == null) {
            return new HttpCookie(new HashMap<>());
        }

        Map<String, String> cookies = Arrays.stream(rawCookies.split("; "))
                .collect(Collectors.toMap(cookie -> cookie.split("=")[0], cookie -> cookie.split("=")[1]));
        return new HttpCookie(cookies);
    }

    public boolean isContains(String cookieName) {
        return cookies.containsKey(cookieName);
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public String findCookie(String key) {
        return cookies.get(key);
    }
}
