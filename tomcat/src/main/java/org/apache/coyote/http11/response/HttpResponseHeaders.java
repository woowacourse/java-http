package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseHeaders {

    private final Map<String, String> values;

    public HttpResponseHeaders() {
        values = new LinkedHashMap<>();
    }

    public void add(final String name, final String value) {
        values.put(name, value);
    }

    public String toResponse() {
        final StringBuilder headerResponse = new StringBuilder();
        for (String headerName : values.keySet()) {
            headerResponse.append(headerName)
                    .append(": ")
                    .append(values.get(headerName))
                    .append(" ")
                    .append("\r\n");
        }
        return headerResponse.toString();
    }
}
