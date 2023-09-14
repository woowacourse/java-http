package org.apache.coyote.request;

import java.util.Map;

public class RequestBody {

    private final Map<String, String> body;

    public RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
