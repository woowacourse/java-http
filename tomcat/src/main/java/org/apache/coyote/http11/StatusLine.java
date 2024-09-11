package org.apache.coyote.http11;

public class StatusLine {

    private final String protocol;
    private final String statusCode;
    private final String statusMessage;

    public StatusLine(String protocol, String statusCode, String statusMessage) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusLine() {
        return protocol + " " + statusCode + " " + statusMessage;
    }
}
