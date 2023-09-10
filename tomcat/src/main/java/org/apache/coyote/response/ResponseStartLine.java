package org.apache.coyote.response;

import org.apache.coyote.common.HttpVersion;

public class ResponseStartLine {

    private final HttpVersion httpVersion;
    private final ResponseStatus status;

    private ResponseStartLine(final HttpVersion httpVersion, final ResponseStatus status) {
        this.httpVersion = httpVersion;
        this.status = status;
    }

    public static ResponseStartLine from(final HttpVersion httpVersion, final ResponseStatus responseStatus) {
        return new ResponseStartLine(httpVersion, responseStatus);
    }

    @Override
    public String toString() {
        return httpVersion + " " + status;
    }
}
