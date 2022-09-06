package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body;

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public String getInfo(final String key) {
        return body.get(key);
    }
}
