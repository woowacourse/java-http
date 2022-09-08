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

    public int getHttpCode() {
        return httpStatus.getCode();
    }

    public String getHttpMessage() {
        return httpStatus.getMessage();
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getResponse(){
        return String.format("%s %d %s\r\n", DEFAULT_PROTOCOL_VERSION, httpStatus.getCode(), httpStatus.getMessage());
    }

}
