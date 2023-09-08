package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> values = new HashMap<>();
        for (String cookie : cookies.split(";")) {
            if (cookie.isEmpty()) {
                break;
            }
            String key = cookie.split("=")[0];
            String value = cookie.split("=")[1];
            values.put(key, value);
        }
        return new HttpCookie(values);
    }

    public void addCookie(String key, String value) {
        values.put(key, value);
    }

    public String getValue(String key) {
        return values.getOrDefault(key, null);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String getCookies() {
        return values.keySet().stream()
                .map(key -> key + "=" + values.get(key))
                .collect(Collectors.joining("&"));
    }
}
