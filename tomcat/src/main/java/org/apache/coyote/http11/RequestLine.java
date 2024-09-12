package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String versionOfProtocol;

    public RequestLine(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");
        method = splitRequestLine[0];
        path = splitRequestLine[1];
        versionOfProtocol = splitRequestLine[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
