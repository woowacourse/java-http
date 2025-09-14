package org.apache.coyote.http11.general;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new HashMap<>());
    }

    public String buildHeaderMessage() {
        StringBuilder result = new StringBuilder();
        for (Entry<String, String> header : headers.entrySet()) {
            result.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        return result.toString();
    }

    public void add(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeaderValueOf(String key) {
        return this.headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
