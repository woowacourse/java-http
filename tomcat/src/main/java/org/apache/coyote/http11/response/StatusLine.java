package org.apache.coyote.http11.response;

public class StatusLine {
    private static final String DELIMITER = " ";
    private static final int PROTOCOL_VERSION_INDEX = 0;
    private static final int STATUS_CODE_INDEX = 1;

    private final String protocolVersion;
    private final HttpStatus httpStatus;

    private StatusLine(String protocolVersion, HttpStatus httpStatus) {
        this.protocolVersion = protocolVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine from(String responseLine) {
        String[] elements = responseLine.split(DELIMITER);
        String protocol = elements[PROTOCOL_VERSION_INDEX];
        String statusCode = elements[STATUS_CODE_INDEX];
        return new StatusLine(protocol, HttpStatus.from(statusCode));
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
