package org.apache.coyote.httpObject;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String path;
    private final String protocol;

    public RequestLine(final String requestLine) {
        final String trimmedRequestLine = requestLine.trim();
        this.httpMethod = HttpHeaderParser.findHttpMethod(trimmedRequestLine);
        this.path = HttpHeaderParser.findPath(trimmedRequestLine);
        this.protocol = HttpHeaderParser.findProtocol(trimmedRequestLine);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }
}
