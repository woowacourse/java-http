package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseHeader {

    private static final String FIELD_CONTENT_FORMAT = "%s: %s\r\n";

    private final Map<String, String> headers;

    public HttpResponseHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String headerLine = String.format(FIELD_CONTENT_FORMAT, entry.getKey(), entry.getValue());
            builder.append(headerLine);
        }
        return builder.toString();
    }
}
