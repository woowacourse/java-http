package com.spring.http.cookie;

import java.util.ArrayList;
import java.util.List;

public final class HttpCookies {

    private final List<HttpCookie> cookies;

    public HttpCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public HttpCookies() {
        this(new ArrayList<>());
    }

    public static HttpCookies from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookies(new ArrayList<>());
        }

        String[] cookiePairs = cookieHeader.split(";");
        List<HttpCookie> cookies = new ArrayList<>();

        parseCookies(cookiePairs, cookies);

        return new HttpCookies(cookies);
    }

    private static void parseCookies(String[] cookiePairs, List<HttpCookie> cookies) {
        for (String pair : cookiePairs) {
            pair = pair.trim();
            if (pair.isEmpty()) {
                continue;
            }

            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                cookies.add(new HttpCookie(kv[0].trim(), kv[1].trim()));
            }
        }
    }

    public HttpCookie getCookie(String name) {
        return cookies.stream()
                .filter(cookie -> cookie.sameName(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No cookie found with name: " + name));
    }

    public void addCookie(HttpCookie cookie) {
        cookies.add(cookie);
    }

    public boolean hasCookie(String name) {
        return cookies.stream()
                .anyMatch(cookie -> cookie.sameName(name));
    }

    @Override
    public String toString() {
        return cookies.stream()
                .map(HttpCookie::toString)
                .reduce((a, b) -> a + "; " + b)
                .orElse("");
    }
}
