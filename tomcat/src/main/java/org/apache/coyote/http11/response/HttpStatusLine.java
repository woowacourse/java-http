package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;

public class HttpStatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public static HttpStatusLine from(final HttpStatus httpStatus) {
        return new HttpStatusLine(HttpVersion.HTTP11, httpStatus);
    }

    private HttpStatusLine(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
