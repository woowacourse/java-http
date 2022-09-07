package org.apache.coyote.http11.model.response;

import java.util.Map;

public class HttpResponseHeader {

    private static final String LINE_BREAK = " \r\n";
    private static final String KEY_VALUE_SEPARATOR = ": ";

    private final Map<String, String> values;

    public HttpResponseHeader(Map<String, String> values) {
        this.values = values;
    }

    public String toResponse() {
        StringBuilder headerResponse = new StringBuilder();
        for (String headerName : values.keySet()) {
            headerResponse.append(headerName)
                    .append(KEY_VALUE_SEPARATOR)
                    .append(values.get(headerName))
                    .append(LINE_BREAK);
        }
        return headerResponse.toString();
    }

    public String getHeader(String headerName) {
        return values.get(headerName);
    }
}
