package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Optional<String> getHeaderValue(String field) {
        if (headers.containsKey(field)) {
            return Optional.of(headers.get(field));
        }
        return Optional.empty();
    }
}
