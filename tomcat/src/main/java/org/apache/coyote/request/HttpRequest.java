package org.apache.coyote.request;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean is(HttpMethod httpMethod, String path) {
        return httpMethod == requestLine.getMethod()
               && path.equals(requestLine.getPath());
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return httpHeader.get(name);
    }
}
