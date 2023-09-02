package org.apache.coyote.http11.message;

import java.util.*;

public class Headers {

    private final Map<String, String> headers;

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers from(List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        lines.stream()
                .map(each -> each.split(": "))
                .forEach(each -> headers.put(each[0], each[1]));

        return new Headers(headers);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public Cookie getCookie() {
        Map<String, String> cookie = new HashMap<>();
        String cookies = headers.get("Cookie");

        Arrays.stream(cookies.split("; "))
                .map(each -> each.split("="))
                .forEach(each -> cookie.put(each[0], each[1]));

        return new Cookie(cookie);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        headers.forEach((key, value) -> stringJoiner.add(key + ": " + value + " "));
        return stringJoiner.toString();
    }
}
