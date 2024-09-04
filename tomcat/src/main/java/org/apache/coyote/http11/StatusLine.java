package org.apache.coyote.http11;

public class StatusLine {
    private final String httpVersion;
    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(String httpVersion, int statusCode, String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public static StatusLine ok(String httpVersion) {
        return new StatusLine(httpVersion, 200, "OK");
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }
}

