package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Http11RequestBody {

    private final Map<String, String> body;

    public Http11RequestBody() {
        this.body = new HashMap<>();
    }

    public Http11RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
