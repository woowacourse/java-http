package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String header) {
        return new HttpCookie(parseHeaderToMap(header));
    }

    private static Map<String, String> parseHeaderToMap(String header) {
        if (header == null) {
            return new HashMap<>();
        }

        String[] cookies = header.split("; ");

        return Arrays.stream(cookies)
                .map(keyValue -> keyValue.split("=", 2))
                .filter(split -> split.length == 2)
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));
    }

    public void put(String key, String value) {
        cookies.put(key, value);
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public String findJSessionId() {
        return cookies.get("JSESSIONID");
    }

}
