package org.apache.coyote.http11.response;

public class Response {

    private final StartLine startLine;
    private final Headers headers;

    public Response(final StartLine startLine, final Headers headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Headers getHeaders() {
        return headers;
    }
}
