package org.apache.coyote.http;

public record HttpServletRequest(
        RequestLine requestLine, HttpHeaders headers, RequestBody body
) {

    public String path() {
        return requestLine.getUri().getPath();
    }
}
