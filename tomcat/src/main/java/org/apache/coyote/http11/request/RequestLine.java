package org.apache.coyote.http11.request;

public class RequestLine {

    private String method;
    private String path;
    private String protocolVersion;

    private RequestLine(final String method, final String path, final String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(final String line) {
        String[] splitedLine = line.split(" ");
        String method = splitedLine[0];
        String path = splitedLine[1];
        String protocolVersion = splitedLine[2];
        return new RequestLine(method, path, protocolVersion);
    }
}
