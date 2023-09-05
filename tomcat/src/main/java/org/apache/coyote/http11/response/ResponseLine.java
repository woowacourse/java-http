package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class ResponseLine {
    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    public ResponseLine(final HttpVersion httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public String parse() {
        return String.join(" ", httpVersion.getVersion(), statusCode.getStatus(), "");
    }
}
