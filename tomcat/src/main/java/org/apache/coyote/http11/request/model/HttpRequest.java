package org.apache.coyote.http11.request.model;

import org.apache.coyote.http11.domain.body.RequestBody;
import org.apache.coyote.http11.domain.header.RequestHeader;
import org.apache.coyote.http11.request.domain.RequestLine;
import org.apache.coyote.http11.request.domain.RequestPath;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader header, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public boolean isDefaultRequestPath() {
        return requestLine.isDefaultPath();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestPath getRequestPath() {
        return requestLine.getRequestPath();
    }

    public RequestHeader getHeader() {
        return header;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
