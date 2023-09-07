package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

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

    public String getValueBy(String key) {
        if (has(key)) {
            return values.get(key);
        }
        throw new IllegalArgumentException("쿠키가 존재하지 않습니다.");
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> getCookies() {
        return values;
    }
}
