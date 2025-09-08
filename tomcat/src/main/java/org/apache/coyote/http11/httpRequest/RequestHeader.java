package org.apache.coyote.http11.httpRequest;

import java.util.Map;
import java.util.Optional;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(
            final Map<String, String> headers
    ) {
        this.headers = headers;
    }

    public Optional<String> findValue(final String name) {
        return Optional.ofNullable(this.headers.get(name));
    }
}
