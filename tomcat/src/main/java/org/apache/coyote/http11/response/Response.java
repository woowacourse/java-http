package org.apache.coyote.http11.response;

public class Response {

    private final StartLine startLine;
    private final Headers headers;
    private final Body body;

    public Response(final StartLine startLine, final Headers headers, final Body body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
