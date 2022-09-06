package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {
    private final Map<String, String> values;

    public HttpRequest(Map<String, String> values) {
        this.values = values;
    }

    public String get(String key) {
        return values.get(key);
    }
}
