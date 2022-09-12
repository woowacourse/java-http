package org.apache.coyote.http11;

public class StatusLine {

    private HttpVersion httpVersion;
    private StatusCode statusCode;

    public StatusLine() {
        this.httpVersion = HttpVersion.HTTP11;
        this.statusCode = StatusCode.OK;
    }

    public void addStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusLineMessage() {
        return httpVersion.getValue() + " " + statusCode.getCode() + " " + statusCode.getMessage();
    }
}
