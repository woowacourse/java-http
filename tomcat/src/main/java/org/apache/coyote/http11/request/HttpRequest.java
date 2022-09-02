package org.apache.coyote.http11.request;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpHeaders headers;

    public HttpRequest(StartLine startLine, HttpHeaders headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public String getPathString() {
        return startLine.getPath();
    }
}
