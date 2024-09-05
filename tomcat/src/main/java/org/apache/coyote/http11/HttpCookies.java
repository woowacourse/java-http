package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookies() {
        this(new HashMap<>());
    }

    private HttpCookies(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public static HttpCookies from(String cookie) {
        return new HttpCookies(parseCookies(cookie));
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

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
