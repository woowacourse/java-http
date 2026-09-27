package org.apache.coyote.http11;

public record HttpRequest(RequestLine requestLine, HttpHeaders headers, byte[] body) {
}
