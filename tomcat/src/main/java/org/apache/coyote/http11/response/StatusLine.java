package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String SP = " ";

    private final String httpVersion;
    private StatusCode statusCode;

    public StatusLine(final String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getStatusLine() {
        return httpVersion + SP
                + statusCode.getCode() + SP
                + statusCode.getText() + SP;
    }

    public void setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
