package org.apache.coyote;

import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.StatusCode;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    public StatusLine(final HttpVersion httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public String getStatusLineMessage() {
        return httpVersion.getValue() + " " + statusCode.getCode() + " " + statusCode.getMessage();
    }
}
