package org.apache.catalina;

public class StatusLine {

    private final String versionOfProtocol;
    private int statusCode;
    private String statusMessage;

    public StatusLine() {
        this.versionOfProtocol = "HTTP/1.1";
        this.statusCode = StatusCode._200.getCode();
        this.statusMessage = StatusCode._200.getMessage();
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
