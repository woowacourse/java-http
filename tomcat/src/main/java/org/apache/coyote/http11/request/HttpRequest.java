package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.ParameterNotFoundException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(final RequestLine requestLine, final RequestHeader header, final RequestBody body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public String findParamFromBody(final String name) {
        return body.findParam(name)
                .orElseThrow(() ->new ParameterNotFoundException(name));
    }

    public RequestMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public RequestHeader getHeader() {
        return header;
    }
}
