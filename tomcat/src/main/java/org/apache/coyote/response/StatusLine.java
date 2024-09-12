package org.apache.coyote.response;

import org.apache.coyote.http.StatusCode;

public class StatusLine {

    private final String versionOfProtocol;
    private int statusCode;
    private String statusMessage;

    public StatusLine() {
        this.versionOfProtocol = "HTTP/1.1";
        this.statusCode = StatusCode.OK.getCode();
        this.statusMessage = StatusCode.OK.getMessage();
    }

    public String getStatusLineResponse() {
        StringBuilder response = new StringBuilder();
        response.append(versionOfProtocol)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage);
        return String.valueOf(response);
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode.getCode();
        this.statusMessage = statusCode.getMessage();
    }
}
