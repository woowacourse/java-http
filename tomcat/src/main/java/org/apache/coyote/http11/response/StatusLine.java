package org.apache.coyote.http11.response;

public class StatusLine {

    private final String protocolVersion;
    private final HttpStatus httpStatus;

    public static StatusLine empty() {
        return new StatusLine(null, null);
    }

    public StatusLine(String protocolVersion, HttpStatus httpStatus) {
        this.protocolVersion = protocolVersion;
        this.httpStatus = httpStatus;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
