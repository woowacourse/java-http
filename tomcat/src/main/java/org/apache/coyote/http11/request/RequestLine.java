package org.apache.coyote.http11.request;

public class RequestLine {
    private HttpMethod method;
    private String path;
    private String protocolVersion;

    public RequestLine(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");

        this.method = HttpMethod.from(splitRequestLine[0]);
        this.path = splitRequestLine[1];
        this.protocolVersion = splitRequestLine[2];
    }

    public HttpMethod method() {
        return method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
