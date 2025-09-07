package org.apache.coyote.http11.message;

import org.apache.coyote.http11.StatusCode;

public class StatusLine {
    private final String httpVersion;
    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(String httpVersion, StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode.getCode();
        this.reasonPhrase = statusCode.getReasonPhrase();
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }
}
