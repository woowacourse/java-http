package org.apache.coyote.http11;

import org.apache.coyote.http11.constant.RequestLine;

public record HttpRequest(
        RequestLine requestLine
) {

    public static HttpRequest from(String request) {
        final String requestLine = RequestLine.extractRequestLine(request);
        return new HttpRequest(RequestLine.from(requestLine));
    }
}
