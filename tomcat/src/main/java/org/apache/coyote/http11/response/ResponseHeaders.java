package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers;
    private final String HEADER_DELIMITER = ": ";

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders create() {
        return new ResponseHeaders(new HashMap<>());
    }

    public ResponseHeaders addHeader(String key, String value) {
        headers.put(key, value);
        return new ResponseHeaders(headers);
    }

    public String getStringHeaders() {
        return headers.entrySet().stream()
                .map(e -> e.getKey() + HEADER_DELIMITER + e.getValue())
                .collect(Collectors.joining("\r\n")) + "\r\n";
    }
}
