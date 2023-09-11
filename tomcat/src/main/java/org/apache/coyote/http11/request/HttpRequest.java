package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {
        return new HttpRequest(requestLine, httpHeader, requestBody);
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public HttpHeader requestHeader() {
        return httpHeader;
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
