package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpVersion;

public class HttpResponseStatusLine {

    private static final HttpVersion DEFAULT_VERSION = HttpVersion.HTTP_1_1;

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    public HttpResponseStatusLine(HttpStatus httpStatus) {
        this.httpVersion = DEFAULT_VERSION;
        this.httpStatus = httpStatus;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String toHttpForm() {
        return httpVersion.toHttpForm() + " " + httpStatus.toHeaderForm();
    }
}
