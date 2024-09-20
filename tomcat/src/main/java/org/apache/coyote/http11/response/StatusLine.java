package org.apache.coyote.http11.response;

public class StatusLine {

    private final String protocolVersion;
    private final HttpStatus status;

    public StatusLine(String protocolVersion, HttpStatus status) {
        this.protocolVersion = protocolVersion;
        this.status = status;
    }

    public static StatusLine createHttp11(HttpStatus status) {
        return new StatusLine("HTTP/1.1", status);
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getStatusMessage() {
        return status.getMessage();
    }

    public String render() {
        return protocolVersion + " " + getStatusCode() + " " + getStatusMessage() + " ";
    }

    @Override
    public String toString() {
        return render();
    }
}
