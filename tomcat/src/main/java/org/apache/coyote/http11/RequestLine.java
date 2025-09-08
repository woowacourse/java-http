package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(String method, String path, String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(String line) {
        return new RequestLine(line.split(" ")[0], line.split(" ")[1], line.split(" ")[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
