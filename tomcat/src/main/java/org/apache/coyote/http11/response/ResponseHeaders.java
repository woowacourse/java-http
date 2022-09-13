package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders initEmpty() {
        return new ResponseHeaders(new HashMap<>());
    }

    public ResponseHeaders addHeader(String key, String value) {
        headers.put(key, value);
        return new ResponseHeaders(headers);
    }

    public String headersToString() {
        return headers.keySet().stream()
                .map(this::headerToString)
                .collect(Collectors.joining("\r\n"));
    }

    private String headerToString(String key) {
        if ("Content-Type".equals(key)) {
            return String.format("Content-Type: text/%s;charset=utf-8 ", headers.get(key));
        }
        return String.format("%s: %s ", key, headers.get(key));
    }
}
