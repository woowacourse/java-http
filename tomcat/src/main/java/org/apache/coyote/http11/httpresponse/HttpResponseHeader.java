package org.apache.coyote.http11.httpresponse;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private final Map<String, String> headers;

    public HttpResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeaders(String key, String value) {
        headers.put(key, value);
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        int size = headers.keySet().size();
        int i = 1;
        for (String key : headers.keySet()) {
            if (i < size) {
                sb.append(key).append(": ").append(headers.get(key)).append(" \r\n");
                size++;
            } else {
                sb.append(key).append(": ").append(headers.get(key));
            }
        }

        return sb.toString();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
