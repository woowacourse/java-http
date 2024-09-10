package org.apache.catalina.http.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpCookies {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    public HttpCookies() {
        this.cookies = new HashMap<>();
    }

    public HttpCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader.isBlank()) {
            return new HttpCookies(cookies);
        }
        String[] keyAndValues = cookieHeader.split(COOKIE_SEPARATOR);
        for (String keyAndValue : keyAndValues) {
            String[] cookie = keyAndValue.split(KEY_VALUE_SEPARATOR);
            cookies.put(cookie[0], cookie[1]);
        }
        return new HttpCookies(cookies);
    }

    public String stringify() {
        StringJoiner joiner = new StringJoiner(COOKIE_SEPARATOR);
        cookies.forEach((key, value) -> joiner.add(key + KEY_VALUE_SEPARATOR + value));
        return joiner.toString();
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(cookies.get(key));
    }
}
