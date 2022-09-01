package org.apache.coyote.http11.model.request;

public class Request {

    private final RequestLine startLine;

    public Request(final String[] lines) {
        this.startLine = new RequestLine(lines[0]);
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }
}
