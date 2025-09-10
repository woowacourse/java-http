package org.apache.coyote.http11;

public class StatusLine {

    private final ProtocolVersion protocolVersion;
    private final ResponseStatus responseStatus;

    public StatusLine(ProtocolVersion protocolVersion, ResponseStatus responseStatus) {
        this.protocolVersion = protocolVersion;
        this.responseStatus = responseStatus;
    }

    public String convertToResponseLine() {
        return protocolVersion.getResponseHeader() + " " + responseStatus.getResponseHeader();
    }
}
