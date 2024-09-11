package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body;

    public HttpRequestBody() {
        this.body = new HashMap<>();
    }

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
