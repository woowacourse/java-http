package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders() {
        this.headers = new HashMap<>();
    }

    public void add(String head, String value) {
        headers.put(head, value);
    }

    public String getValue(String key) {
        return headers.get(key);
    }

    public boolean containValue(String key) {
        return headers.containsKey(key);
    }

}
