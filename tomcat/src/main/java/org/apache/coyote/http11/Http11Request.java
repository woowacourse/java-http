package org.apache.coyote.http11;

public class Http11Request {

    private final RequestLine requestLine;
    private final Headers headers;

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
