package org.apache.catalina.response;

import org.apache.catalina.ProtocolVersion;
import org.apache.catalina.http.StatusCode;

public class StatusLine {

    private final ProtocolVersion versionOfProtocol;
    private StatusCode statusCode;

    public StatusLine() {
        this.versionOfProtocol = ProtocolVersion.HTTP1_1;
        this.statusCode = StatusCode.OK;
    }

    public String getResponse() {
        return "%s %s %s".formatted(versionOfProtocol.getValue(), statusCode.getCode(), statusCode.getMessage());
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
