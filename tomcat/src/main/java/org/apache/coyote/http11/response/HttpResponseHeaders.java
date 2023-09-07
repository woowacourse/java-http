package org.apache.coyote.http11.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HttpResponseHeaders {

    private Map<String, String> headers;

    private HttpResponseHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public HttpResponseHeaders() {
        this(new HashMap<>());
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
}
