package org.apache.coyote.response.responseLine;

import org.apache.coyote.protocolVersion.ProtocolVersion;
import org.apache.coyote.util.HttpStatus;

public class ResponseLine {

    public static final String BLANK = " ";

    private final ProtocolVersion protocolVersion;

    private HttpStatus httpStatus;

    public ResponseLine(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String toCombinedResponse() {
        return protocolVersion.getCombinedProtocolVersion() + BLANK + httpStatus.getCombinedHttpStatus();
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
