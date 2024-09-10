package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        return Integer.parseInt(
                Optional.ofNullable(headers.get("Content-Length"))
                        .orElse("0")
        );
    }
}
