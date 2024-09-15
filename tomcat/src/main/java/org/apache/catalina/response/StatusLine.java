package org.apache.catalina.response;

import org.apache.catalina.http11.ProtocolVersion;
import org.apache.catalina.http.StatusCode;

public class StatusLine {

    private final ProtocolVersion versionOfProtocol;
    private StatusCode statusCode;

    public StatusLine() {
        this.versionOfProtocol = ProtocolVersion.HTTP1_1;
        this.statusCode = StatusCode.OK;
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();
        response.append(versionOfProtocol.getValue())
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage());
        return String.valueOf(response);
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
