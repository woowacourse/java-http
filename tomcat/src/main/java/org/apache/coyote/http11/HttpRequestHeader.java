package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> headers;

    public HttpRequestHeader(List<String> headerValues) {
        this.headers = parseHeaderValues(headerValues);
    }

    private Map<String, String> parseHeaderValues(List<String> headerValues) {
        Map<String, String> values = new HashMap<>();
        for (String headerValue : headerValues) {
            String[] keyAndValue = headerValue.split(KEY_VALUE_SEPARATOR);
            values.put(keyAndValue[KEY], keyAndValue[VALUE]);
        }
        return values;
    }
}
