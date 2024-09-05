package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private Map<String, String> httpRequestHeaders;

    public HttpRequestHeader() {
        this.httpRequestHeaders = new HashMap<>();
    }

    public void add(String key, String value) {
        this.httpRequestHeaders.put(key, value);
    }

    public String findBy(String key) {
        return this.httpRequestHeaders.get(key);
    }

    public boolean containsKey(String key) {
        return this.httpRequestHeaders.containsKey(key);
    }
}
