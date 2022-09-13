package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpStatusLine {

    private final String version;
    private final HttpStatus httpStatus;

    public HttpStatusLine(final String version, final HttpStatus httpStatus) {
        this.version = version;
        this.httpStatus = httpStatus;
    }

    public HttpStatusLine(final HttpStatus httpStatus) {
        this("HTTP/1.1", httpStatus);
    }

    @Override
    public String toString() {
        return version + " "
                + httpStatus.getStatusCode() + " "
                + httpStatus.getMessage() + " ";
    }
}
