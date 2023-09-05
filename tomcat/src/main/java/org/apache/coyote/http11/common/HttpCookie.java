package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String header) {
        String[] cookies = header.split("; ");

        Map<String, String> map = Arrays.stream(cookies)
                .map(keyValue -> keyValue.split("=", 2))
                .filter(split -> split.length == 2)
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));
        return new HttpCookie(map);
    }

    public void put(final String key, final String value) {
        cookies.put(key, value);
    }

    public String get(final String key) {
        return cookies.get(key);
    }

}
