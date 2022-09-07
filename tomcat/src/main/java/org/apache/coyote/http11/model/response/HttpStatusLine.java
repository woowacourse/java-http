package org.apache.coyote.http11.model.response;

import org.apache.coyote.http11.model.HttpStatus;

public class HttpStatusLine {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final String protocolVersion;
    private final HttpStatus httpStatus;

    public HttpStatusLine(HttpStatus httpStatus) {
        this.protocolVersion = PROTOCOL_VERSION;
        this.httpStatus = httpStatus;
    }

    public String toResponse() {
        return protocolVersion + " " + httpStatus.toResponseMessage() + " ";
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
