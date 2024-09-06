package org.apache.coyote.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponseStartLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatus httpStatus;

    public HttpResponseStartLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static HttpResponseStartLine defaultLine() {
        return new HttpResponseStartLine(HttpStatus.OK);
    }

    @Override
    public String toString() {
        return HTTP_VERSION + " " + httpStatus.getCode() + " " + httpStatus.getText() + " ";
    }
}
