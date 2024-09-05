package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public static HttpCookie from(String cookie) {
        return new HttpCookie(parseCookies(cookie));
    }

    private static Map<String, String> parseCookies(String cookiesString) {
        String[] cookieParts = cookiesString.split("; ");
        if (cookieParts.length == 0) {
            return new HashMap<>();
        }

        return Arrays.stream(cookieParts)
                .map(part -> part.split("="))
                .collect(Collectors.toMap(part -> part[0], part -> part[1]));
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void removeCookie(String key) {
        cookies.remove(key);
    }

    public String getCookieString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
