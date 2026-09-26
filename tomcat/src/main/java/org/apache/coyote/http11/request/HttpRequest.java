package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.requestLine = new RequestLine(bufferedReader);
        this.headers = new RequestHeaders(bufferedReader);
        this.body = new RequestBody(bufferedReader, headers);
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
