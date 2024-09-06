package org.apache.coyote.http11.request.body;

import java.util.Map;

public class RequestBody {

    private final Map<String, String> params;

    public RequestBody(Map<String, String> params) {
        this.params = params;
    }

    public String get(String key) {
        return params.get(key);
    }
}
