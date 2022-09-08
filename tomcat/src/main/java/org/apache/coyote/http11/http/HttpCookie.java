package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private Map<String, String> values;

    public HttpCookie() {
        values = new HashMap<>();
    }

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(String key, String value) {
        return new HttpCookie(Map.of(key, value));
    }

    public String getJSessionId() {
        return values.get("JSESSIONID");
    }

    public void putAll(Map<String, String> map) {
        values.putAll(map);
    }
}
