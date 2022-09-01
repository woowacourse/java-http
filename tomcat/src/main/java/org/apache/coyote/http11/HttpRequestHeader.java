package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    public HttpRequestHeader(List<String> headerValues) {
        this.headers = parseHeaderValues(headerValues);
    }

    private Map<String, String> parseHeaderValues(List<String> headerValues) {
        Map<String, String> values = new HashMap<>();
        for (String headerValue : headerValues) {
            String[] keyAndValue = headerValue.split(": ");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return values;
    }
}
