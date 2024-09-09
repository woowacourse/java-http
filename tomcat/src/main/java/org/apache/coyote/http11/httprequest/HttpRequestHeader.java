package org.apache.coyote.http11.httprequest;

import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    public HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public String getValue(String key) {
        return headers.get(key);
    }
}
