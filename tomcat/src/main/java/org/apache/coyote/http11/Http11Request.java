package org.apache.coyote.http11;

public class Http11Request {

    private RequestLine requestLine;
    private Headers headers;

    public Http11Request(final RequestLine requestLine, final Headers headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParam(final String queryParamName) {
        return requestLine.getQueryParam(queryParamName);
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getHeader(String headerName) {
        return headers.getHeader(headerName);
    }
}
