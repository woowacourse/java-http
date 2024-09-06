package org.apache.coyote.http11.request.header;

import java.util.Map;
import java.util.Optional;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Optional<String> get(String header) {
        return Optional.ofNullable(headers.get(header));
    }
}
