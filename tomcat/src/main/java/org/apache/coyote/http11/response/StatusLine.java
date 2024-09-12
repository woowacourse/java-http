package org.apache.coyote.http11.response;

public class StatusLine {
    private final String version;
    private final HttpStatusCode statusCode;
    private final String statusMessage;

    public StatusLine(String version, HttpStatusCode statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
