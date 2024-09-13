package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.HttpVersion;

public class HttpStatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public HttpStatusLine(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public String convertHttpStatusLineMessage() {
        return httpVersion.getValue() + " " + httpStatus.getCode() + " " + httpStatus.getDescription();
    }
}
