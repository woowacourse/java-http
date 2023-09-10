package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String SP = " ";

    private final String httpVersion;
    private final StatusCode statusCode;

    public StatusLine(final String httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public String getStatusLine() {
        return httpVersion + SP
                + statusCode.getCode() + SP
                + statusCode.getText() + SP;
    }
}
