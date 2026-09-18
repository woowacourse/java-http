package org.apache.coyote.http;

import java.util.Optional;

public record HttpServletRequest(
        RequestLine requestLine, HttpHeaders headers, RequestBody body
) {

    public String path() {
        return requestLine.getUri().getPath();
    }

    public Optional<String> cookie(String key) {
        return headers.getCookie(key);
    }
}
