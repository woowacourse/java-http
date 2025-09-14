package org.apache.coyote.http11.request_response.response;

import org.apache.coyote.http11.request_response.HttpStatus;

public class ResponseLine {

    private final String protocolVersion;
    private final HttpStatus status;

    public ResponseLine(String protocolVersion, HttpStatus status) {
        this.protocolVersion = protocolVersion;
        this.status = status;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getStatusMessage() {
        return status.getMessage();
    }
}
