package org.apache.coyote.http11.response;

import org.apache.coyote.http11.constant.HttpStatus;

public class HttpStatusLine {

    private final String protocolVersion;
    private int statusCode;
    private String statusMessage;

    public HttpStatusLine() {
        this.protocolVersion = "HTTP/1.1";
    }

    public void status(HttpStatus status) {
        this.statusCode = status.getStatusCode();
        this.statusMessage = status.getMessage();
    }

    public String getStatusLineMessage() {
        return protocolVersion + " " + statusCode + " " + statusMessage;
    }
}
