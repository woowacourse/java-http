package org.apache.coyote.http11;

public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String toMessage() {
        return HTTP_VERSION + " " + statusCode + " " + reasonPhrase + " ";
    }
}
