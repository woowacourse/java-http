package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String SP = " ";

    private final String protocolVersion;
    private StatusCode statusCode;

    public StatusLine(final String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getStatusLine() {
        return protocolVersion + SP
                + statusCode.getCode() + SP
                + statusCode.getMessage() + SP;
    }

    public void setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
