package org.apache.coyote.http11.common.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpHeaderName;

public class ResponseHeaders {

    private static final String DELIMITER = ": ";

    private Map<String, String> headers;

    private ResponseHeaders() {
        headers = new LinkedHashMap<>();
    }

    public static ResponseHeaders create() {
        return new ResponseHeaders();
    }

    public void addHeader(HttpHeaderName header, String value) {
        headers.put(header.getName(), value);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + DELIMITER + entry.getValue() + " ")
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
