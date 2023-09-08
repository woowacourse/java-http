package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeader;
import org.apache.coyote.http11.request.line.RequestLine;

public class Request {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    private Request(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request of(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        return new Request(requestLine, requestHeader, requestBody);
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

}
