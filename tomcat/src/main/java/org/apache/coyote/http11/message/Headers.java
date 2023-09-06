package org.apache.coyote.http11.message;

import java.util.*;

public class Headers {

    private final Map<String, String> mappings;

    public Headers(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public static Headers ofEmpty() {
        return new Headers(new HashMap<>());
    }

    public void addHeader(HttpHeaders key, String value) {
        mappings.put(key.value(), value);
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
