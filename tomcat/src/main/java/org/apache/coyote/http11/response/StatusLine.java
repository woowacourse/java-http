package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String SEPARATOR = " ";

    private final String httpVersion;
    private final String statusCode;
    private final String reasonPhrase;

    public StatusLine(final String httpVersion, final String statusCode, final String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String format() {
        return String.join(SEPARATOR, httpVersion, statusCode, reasonPhrase);
    }
}
