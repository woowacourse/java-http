package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestLine requestLine,
            final Headers requestHeader,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.headers = requestHeader;
        this.requestBody = requestBody;
    }

    public boolean isGet() {
        return requestLine.getHttpMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return requestLine.getHttpMethod() == HttpMethod.POST;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
