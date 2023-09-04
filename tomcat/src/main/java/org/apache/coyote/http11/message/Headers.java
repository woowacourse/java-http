package org.apache.coyote.http11.message;

import java.util.*;

public class Headers {

    private final Map<String, String> mappings;

    private Headers(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public static Headers fromMap(Map<String, String> mappings) {
        return new Headers(mappings);
    }

    public static Headers fromLines(List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        lines.stream()
                .map(each -> each.split(": "))
                .forEach(each -> headers.put(each[0], each[1]));

        return new Headers(headers);
    }

    public String get(String key) {
        return mappings.get(key);
    }

    public Cookie getCookie() {
        Map<String, String> cookie = new HashMap<>();
        String cookies = mappings.get("Cookie");

        if (Objects.nonNull(cookies)) {
            Arrays.stream(cookies.split("; "))
                    .map(each -> each.split("="))
                    .forEach(each -> cookie.put(each[0], each[1]));
        }
        return new Cookie(cookie);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        mappings.forEach((key, value) -> stringJoiner.add(key + ": " + value + " "));
        return stringJoiner.toString();
    }
}
