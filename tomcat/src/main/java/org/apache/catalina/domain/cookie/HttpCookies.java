package org.apache.catalina.domain.cookie;

import java.util.ArrayList;
import java.util.List;

public record HttpCookies(List<HttpCookie> cookies) {

    public HttpCookies() {
        this(new ArrayList<>());
    }

    public static HttpCookies from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookies(List.of());
        }

        String[] cookiePairs = cookieHeader.split("; ");
        List<HttpCookie> cookies = new java.util.ArrayList<>();

        for (String pair : cookiePairs) {
            pair = pair.trim();
            if (pair.isEmpty()) {
                continue;
            }
            
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                cookies.add(new HttpCookie(kv[0].trim(), kv[1].trim()));
                continue;
            }
            if (kv.length == 1) {
                cookies.add(new HttpCookie(kv[0].trim(), ""));
            }
        }

        return new HttpCookies(cookies);
    }

    public HttpCookie getCookie(String name) {
        return cookies.stream()
                .filter(cookie -> cookie.sameName(name))
                .findFirst()
                .orElse(null);
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
