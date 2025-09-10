package org.apache.coyote.http11.general;

import java.util.Map;

public class HttpBody {

    private final Map<String, String> body;

    public HttpBody(Map<String, String> body) {
        this.body = body;
    }

    public String get(String key) {
        return this.body.get(key);
    }
}
