package org.apache.coyote.http11.httprequest;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body;

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public boolean containsBody(String key) {
        return body.containsKey(key);
    }

    public String getBodyValue(String key) {
        return body.get(key);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
