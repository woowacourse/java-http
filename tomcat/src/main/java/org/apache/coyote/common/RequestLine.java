package org.apache.coyote.common;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String protocol;

    public RequestLine(String method, String uri, String protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "method='" + method + '\'' +
               ", uri='" + uri + '\'' +
               ", protocol='" + protocol + '\'' +
               '}';
    }
}
