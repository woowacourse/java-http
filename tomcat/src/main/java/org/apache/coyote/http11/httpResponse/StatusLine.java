package org.apache.coyote.http11.httpResponse;

import org.apache.coyote.http11.general.HttpProtocolVersion;

public class StatusLine {

    private final HttpProtocolVersion protocolVersion;
    private final HttpStatus status;

    public StatusLine(HttpProtocolVersion protocolVersion, HttpStatus status) {
        this.protocolVersion = protocolVersion;
        this.status = status;
    }

    public int getStatusCode() {
        return this.status.getCode();
    }

    public String getStatusMessage() {
        return this.status.getMessage();
    }
}
