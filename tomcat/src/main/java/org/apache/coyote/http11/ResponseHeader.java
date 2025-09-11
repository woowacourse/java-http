package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
