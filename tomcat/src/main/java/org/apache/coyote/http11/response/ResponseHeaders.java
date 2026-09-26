package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void add(final String name, final String value) {
        headers.put(name, value);
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public boolean contains(final String name) {
        return headers.containsKey(name);
    }
}
