package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final Status status;

    public StatusLine(final HttpVersion httpVersion, final Status status) {
        this.httpVersion = httpVersion;
        this.status = status;
    }

    public String getStatusLineForResponse() {
        return httpVersion.getValue() + " " + status.getStatusForResponse();
    }
}
