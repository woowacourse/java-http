package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        this.requestLine = new RequestLine(inputStream);
        this.headers = new RequestHeaders(inputStream);
        this.body = new RequestBody(inputStream, headers);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }
}
