package org.apache.coyote.http11.httprequest;

import org.apache.coyote.http11.InputReader;

import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(InputReader inputReader) throws IOException {
        this.requestLine = new RequestLine(inputReader.readRequestLine());
        this.headers = new RequestHeaders(inputReader.readHeaders());
        this.body = new RequestBody(inputReader.readBody(headers.getContentLength()));
    }

    public boolean isGetMethod() {
        return requestLine.isGetMethod();
    }

    public boolean isPostMethod() {
        return requestLine.isPostMethod();
    }

    public boolean hasQueryParameter() {
        return requestLine.hasQueryParameter();
    }

    public QueryParameter getQueryParameter() {
        return requestLine.getQueryParameter();
    }

    public boolean hasJSessionCookie() {
        return headers.hasJSessionCookie();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getBody() {
        return body.getValue();
    }
}
