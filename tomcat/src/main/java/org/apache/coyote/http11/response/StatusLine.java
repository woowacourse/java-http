package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String SP = " ";

    private final String protocolVersion;
    private final StatusCode statusCode;

    public StatusLine(final String protocolVersion, final StatusCode statusCode) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
    }

    public String getStatusLine() {
        return protocolVersion + SP
                + statusCode.getCode() + SP
                + statusCode.getMessage() + SP;
    }
}
