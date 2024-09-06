package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponseHeaders {

    private final Map<String, String> headers;

    public HttpResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String[] getKeys() {
        return headers.keySet().toArray(new String[0]);
    }

    public String getValue(String key) {
        return headers.get(key);
    }
}
