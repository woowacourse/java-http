package org.apache.coyote.http11.Response;

public class StatusLine {

    private static final String STATUS_LINE_DELIMITER = " ";

    private final String httpVersion;
    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(final String httpVersion, final int statusCode, final String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getResponse() {
        return String.join(STATUS_LINE_DELIMITER,
                httpVersion,
                String.valueOf(statusCode),
                reasonPhrase,
                "");
    }
}
