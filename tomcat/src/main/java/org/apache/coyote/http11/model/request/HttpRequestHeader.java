package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.exception.InvalidHttpRequestException;

public class HttpRequestHeader {

    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> values;

    public HttpRequestHeader(List<String> headerValues) {
        this.values = parseHeaderValues(headerValues);
    }

    private Map<String, String> parseHeaderValues(List<String> headerValues) {
        validateEmptyHeader(headerValues);
        Map<String, String> values = new HashMap<>();
        for (String headerValue : headerValues) {
            String[] keyAndValue = headerValue.split(KEY_VALUE_SEPARATOR);
            values.put(keyAndValue[KEY], keyAndValue[VALUE]);
        }
        return values;
    }

    private static void validateEmptyHeader(List<String> headerValues) {
        if (headerValues.isEmpty()) {
            throw new InvalidHttpRequestException("Http Request Header가 비어있습니다.");
        }
    }

    public String getHeaderValue(String key) {
        return values.get(key);
    }

    public boolean hasNotHeader(String key) {
        return !values.containsKey(key);
    }
}
