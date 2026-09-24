package org.apache.coyote.http11.response;

public class StatusLine {

    private String version;
    private int statusCode;
    private String reasonPhrase;

    public StatusLine(
            final String version,
            final int statusCode,
            final String reasonPhrase
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String toResponseLine() {
        return version + " " + statusCode + " " + reasonPhrase;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
