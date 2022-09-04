package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    // todo: body 추가

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> header) {
        return new HttpRequest(
                RequestLine.of(header.get(0)),
                RequestHeaders.of(header.subList(1, header.size()))
        );
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }
}
