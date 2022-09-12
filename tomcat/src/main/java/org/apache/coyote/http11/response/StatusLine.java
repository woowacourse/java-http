package org.apache.coyote.http11.response;

public class StatusLine {
    private static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";

    private final String protocolVersion;
    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this(DEFAULT_PROTOCOL_VERSION, httpStatus);
    }

    public StatusLine(String protocolVersion, HttpStatus httpStatus) {
        this.protocolVersion = protocolVersion;
        this.httpStatus = httpStatus;
    }

    public String getResponse() {
        return String.format("%s %d %s\r\n", protocolVersion, httpStatus.getCode(), httpStatus.getMessage());
    }

}
