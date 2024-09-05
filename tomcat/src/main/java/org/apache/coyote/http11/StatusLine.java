package org.apache.coyote.http11;

public class StatusLine {

    private final String httpVersion;
    private final int statusCode;
    private final String statusMessage;

    public StatusLine() {
        this("HTTP/1.1", 200, "OK");
    }

    private StatusLine(String httpVersion, int statusCode, String statusMessage) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusLineMessage() {
        return String.join(" ", httpVersion, String.valueOf(statusCode), statusMessage, "");
    }
}
