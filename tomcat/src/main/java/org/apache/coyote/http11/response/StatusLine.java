package org.apache.coyote.http11.response;

public class StatusLine {
    private static final String DELIMITER = " ";
    private static final int PROTOCOL_VERSION_INDEX = 0;
    private static final int STATUS_CODE_INDEX = 1;

    private final String protocolVersion;
    private final HttpStatus httpStatus;

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
