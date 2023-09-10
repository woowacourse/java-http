package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public RequestHeader requestHeader() {
        return requestHeader;
    }

    public RequestBody requestBody() {
        return requestBody;
    }

    public boolean methodIsEqualTo(HttpMethod httpMethod) {
        return this.requestLine.method().equals(httpMethod);
    }

    public Protocol getProtocol() {
        return requestLine.protocol();
    }

}
