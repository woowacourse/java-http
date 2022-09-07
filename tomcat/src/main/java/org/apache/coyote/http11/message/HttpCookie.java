package org.apache.coyote.http11.message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private Map<String, String> values;

    public HttpCookie() {
        this.values = new HashMap<>();
    }
    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static UUID create() {
        return UUID.randomUUID();
    }

    public static HttpCookie of(String key, String value) {
        return new HttpCookie(Map.of(key, value));
    }
}
