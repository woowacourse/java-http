package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(final RequestLine requestLine, final RequestHeader header, final RequestBody body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest parse(final String startLine, final List<String> header, final String body) {
        return new HttpRequest(RequestLine.parse(startLine), RequestHeader.parse(header), RequestBody.parse(body));
    }

    public RequestMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Params getParamsFromUri() {
        return requestLine.getParams();
    }

    public Params getParamsFromBody() {
        return body.getParams();
    }
}
