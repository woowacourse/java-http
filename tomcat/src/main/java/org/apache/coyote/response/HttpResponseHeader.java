package org.apache.coyote.response;

import java.util.Map;

public class HttpResponseHeader {

    private final Map<String, String> headers;

    public HttpResponseHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    public String getResponseHeader() {
        StringBuilder headerBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            headerBuilder.append(key).append(": ").append(value).append(" \r\n");
        }
        return headerBuilder.toString();
    }
}
