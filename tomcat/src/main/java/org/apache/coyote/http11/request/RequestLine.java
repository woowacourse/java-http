package org.apache.coyote.http11.request;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String versionOfProtocol;

    public RequestLine(HttpMethod method, String path, String versionOfProtocol) {
        this.method = method;
        this.path = path;
        this.versionOfProtocol = versionOfProtocol;
    }
}
