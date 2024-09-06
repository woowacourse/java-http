package org.apache.coyote.http11.response.domain;

import org.apache.coyote.http11.domain.protocolVersion.ProtocolVersion;

public class ResponseLine {

    private final ProtocolVersion protocolVersion;
    private final HttpStatus httpStatus;

    public ResponseLine(ProtocolVersion protocolVersion, HttpStatus httpStatus) {
        this.protocolVersion = protocolVersion;
        this.httpStatus = httpStatus;
    }

    public String toCombinedResponse() {
        return protocolVersion.getCombinedProtocolVersion() + " " + httpStatus.getCombinedHttpStatus();
    }
}
