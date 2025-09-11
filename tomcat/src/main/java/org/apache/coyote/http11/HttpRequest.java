package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpRequest(
            final RequestLine requestLine,
            final HttpHeaders headers,
            final byte[] body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public String getBodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }
}
