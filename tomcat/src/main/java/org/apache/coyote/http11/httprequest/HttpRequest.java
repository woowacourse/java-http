package org.apache.coyote.http11.httprequest;

import org.apache.coyote.http11.InputReader;

import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(InputReader inputReader) throws IOException {
        this.requestLine = new RequestLine(inputReader.readRequestLine());
        this.headers = new Headers(inputReader.readHeaders());
        this.body = new Body(inputReader.readBody(headers.getContentLength()));
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }
}
