package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        RequestLine requestLine = RequestLine.from(reader);
        RequestHeader requestHeader = RequestHeader.from(reader);
        RequestBody requestBody = RequestBody.of(requestHeader.contentLength(), reader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    public boolean isGet() {
        return "GET".equals(requestLine.httpMethod());
    }

    public boolean isPost() {
        return "POST".equals(requestLine.httpMethod());
    }

    public String getRequestPath() {
        return requestLine.uriInfo().path();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
