package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private final Map<String, String> body;

    public RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return new HashMap<>(body);
    }
}
