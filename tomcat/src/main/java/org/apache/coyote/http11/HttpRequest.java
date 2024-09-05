package org.apache.coyote.http11;

import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(InputReader inputReader) throws IOException {
        this.requestLine = new RequestLine(inputReader.readRequestLine());
        this.headers = new Headers(inputReader.readHeaders());
        this.body = new Body(inputReader.readBody());
    }

    public String getUri() {
        return requestLine.getUri();
    }
}
