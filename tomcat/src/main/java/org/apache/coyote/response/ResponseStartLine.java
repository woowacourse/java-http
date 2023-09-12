package org.apache.coyote.response;

import org.apache.coyote.common.HttpVersion;

public class ResponseStartLine {

    private final HttpVersion httpVersion;
    private ResponseStatus status;

    private ResponseStartLine(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        this.status = ResponseStatus.OK;
    }

    public static ResponseStartLine from(final HttpVersion httpVersion) {
        return new ResponseStartLine(httpVersion);
    }

    public void setStatus(final ResponseStatus responseStatus) {
        this.status = responseStatus;
    }

    @Override
    public String toString() {
        return httpVersion + " " + status;
    }
}
