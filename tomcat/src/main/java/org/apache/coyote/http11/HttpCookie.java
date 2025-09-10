package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie fromHeader(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new HttpCookie(new HashMap<>());
        }
        return new HttpCookie(parseCookie(cookieHeader));
    }

    private static Map<String, String> parseCookie(final String cookieHeader) {
        return Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(cookie -> cookie.split("=", 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(
                        HashMap::new,
                        (map, keyValue) -> map.put(keyValue[0], keyValue[1]),
                        Map::putAll
                );
    }

    public String getValue(final String name) {
        return cookies.get(name);
    }

    public void add(String name, String value) {
        cookies.put(name, value);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }

    public Map<String, String> getAll() {
        return new HashMap<>(cookies);
    }
}
