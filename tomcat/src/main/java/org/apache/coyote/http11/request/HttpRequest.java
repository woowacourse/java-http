package org.apache.coyote.http11.request;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(final RequestLine requestLine, final RequestHeader header, final RequestBody body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public RequestMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Params getParams() {
        return body.getParams();
    }

    public RequestHeader getHeader() {
        return header;
    }
}
