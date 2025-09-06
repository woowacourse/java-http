package org.apache.coyote.request;

import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader,
                       final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
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

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
