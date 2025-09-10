package org.apache.coyote.http11.httpResponse;

import org.apache.coyote.http11.httpRequest.ProtocolVersion;

public class StatusLine {

    private final ProtocolVersion protocolVersion;
    private final HttpStatus httpStatus;

    private StatusLine(
            final ProtocolVersion protocolVersion,
            final HttpStatus httpStatus
    ) {
        this.protocolVersion = protocolVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine build(
            final ProtocolVersion protocolVersion,
            final HttpStatus httpStatus
    ) {
        return new StatusLine(protocolVersion, httpStatus);
    }

    @Override
    public String toString() {
        return protocolVersion.getVersion() + " " + httpStatus.toString();
    }
}
