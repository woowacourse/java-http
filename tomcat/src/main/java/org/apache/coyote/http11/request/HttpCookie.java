package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public static HttpCookie from(String input) {
        HashMap<String, String> values = new HashMap<>();
        for (String value : input.split("; ")) {
            String[] parsedCookie = value.split("=");
            values.put(parsedCookie[0], parsedCookie[1]);
        }
        return new HttpCookie(values);
    }

    public boolean exists() {
        return !values.isEmpty();
    }

    public String getValue(String key) {
        return values.get(key);
    }
}
