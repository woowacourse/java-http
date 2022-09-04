package org.apache.coyote.http11.httpmessage.statusline;

import org.apache.coyote.http11.httpmessage.HttpVersion;

public class StatusLine {
    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public String parseToString() {
        return httpVersion.getVersion()
                + " "
                + httpStatus.getStatusCode()
                + " "
                + httpStatus.getReasonPhrase();
    }
}
