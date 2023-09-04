package org.apache.coyote.http.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookies {

    private List<Cookie> cookies;

    private Cookies(List<Cookie> cookies) {
        this.cookies = new ArrayList<>(cookies);
    }

    public static Cookies from() {
        return new Cookies(new ArrayList<>());
    }

    public static Cookies from(Map<String, List<String>> header) {
        List<String> cookie = header.get("Cookie");
        if (cookie != null) {
            return new Cookies(decodeCookies(cookie.get(0)));
        }
        return from();
    }

    private static List<Cookie> decodeCookies(String cookiesString) {
        return Arrays.stream(cookiesString.split(";"))
                     .map(String::strip)
                     .map(cookieString -> cookiesString.split("="))
                     .filter(keyValue -> keyValue.length == 2)
                     .map(keyValue -> new Cookie(keyValue[0], keyValue[1]))
                     .collect(Collectors.toList());
    }

    public Optional<Cookie> findCookie(String key) {
        return cookies.stream()
                      .filter(cookie -> cookie.hasKey(key))
                      .findFirst();
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }
}
