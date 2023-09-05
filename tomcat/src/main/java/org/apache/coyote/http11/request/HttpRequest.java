package org.apache.coyote.http11.request;

public class HttpRequest {


    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(String requestLine, String requestHeader, String requestBody) {
        return new HttpRequest(
                RequestLine.from(requestLine),
                RequestHeader.from(requestHeader),
                RequestBody.from(requestBody)
        );
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
