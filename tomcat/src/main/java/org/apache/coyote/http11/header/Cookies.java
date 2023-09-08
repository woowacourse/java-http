package org.apache.coyote.http11.header;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    private Cookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookies from(HttpHeader header) {
        Map<String, String> cookies = Arrays.stream(header.getValue().split(COOKIE_SEPARATOR))
                .filter(it -> !it.isBlank())
                .collect(Collectors.toMap(
                        it -> it.split(VALUE_SEPARATOR)[0],
                        it -> it.split(VALUE_SEPARATOR)[1]
                ));
        return new Cookies(cookies);
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public HttpHeader toHeader(String name) {
        return new HttpHeader(name, this.flatten());
    }

    private String flatten() {
        StringBuilder value = new StringBuilder();
        for (var cookie : cookies.entrySet()) {
            value.append(cookie.getKey())
                    .append(VALUE_SEPARATOR)
                    .append(cookie.getValue());
        }
        return value.toString();
    }
}
