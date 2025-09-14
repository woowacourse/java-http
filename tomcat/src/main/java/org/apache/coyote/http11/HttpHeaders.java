package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    
    private final Map<String, String> headers;
    
    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders add(String name, String value) {
        headers.put(name, value);
        return this;
    }
    
    public String get(String name) {
        return headers.get(name);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey())
              .append(": ")
              .append(entry.getValue())
              .append("\r\n");
        }
        return sb.toString();
    }

    public static HttpHeaders empty() {
        return new HttpHeaders()
            .add("Content-Type", "text/html;charset=utf-8")
            .add("Content-Length", "0");
    }
}
