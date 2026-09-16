package org.apache.coyote.http;

public record HttpServletRequest(
        RequestLine requestLine, HttpHeaders headers, RequestBody body
) {

    public static HttpServletRequest of(RequestLine requestLine, HttpHeaders headers, RequestBody body) {
        return new HttpServletRequest(requestLine, headers, body);
    }
}
