package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> body = new HashMap<>();

    public void add(final String key, final String value) {
        body.put(key, value);
    }

    public String findByKey(final String key) {
        return body.get(key);
    }
}
