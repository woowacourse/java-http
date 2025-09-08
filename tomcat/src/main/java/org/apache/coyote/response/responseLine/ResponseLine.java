package org.apache.coyote.response.responseLine;

import org.apache.coyote.request.requestLine.protocolVersion.Protocol;
import org.apache.coyote.request.requestLine.protocolVersion.ProtocolVersion;
import org.apache.coyote.request.requestLine.protocolVersion.Version;

public class ResponseLine {

    private ProtocolVersion protocolVersion;
    private HttpStatus httpStatus;

    public ResponseLine() {}

    public String combine() {
        return protocolVersion.combine() + " " + httpStatus.combine();
    }

    public ResponseLine init(final HttpStatus httpStatus) {
        this.protocolVersion = ProtocolVersion.of(Protocol.HTTP, Version.fromHTTP1());
        this.httpStatus = httpStatus;

        return this;
    }
}
