package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> value;

    public HttpHeaders(Map<String, String> value) {
        this.value = value;
    }

    public static HttpHeaders from(List<String> lines) {
        Map<String, String> value = new HashMap<>();

        for (String line : lines) {
            String[] split = line.split(": ");
            validate(split);
            value.put(split[0], split[1]);
        }

        return new HttpHeaders(value);
    }

    private static void validate(String[] split) {
        if (split.length != 2) {
            throw new IllegalArgumentException("key value not matched");
        }
        if (split[0].isBlank() || split[1].isBlank()) {
            throw new IllegalArgumentException("key value cant not be blank");
        }
    }

    public boolean contains(String key) {
        return value.containsKey(key);
    }

    public String find(String key) {
        return value.get(key);
    }

    public void set(String key, String value) {
        this.value.put(key, value);
    }
}
