package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponseStatusLine {

    private final String httpVersion;
    private final HttpStatus httpStatus;

    private HttpResponseStatusLine(final String httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static HttpResponseStatusLine of(final String httpVersion, final HttpStatus httpStatus) {
        return new HttpResponseStatusLine(httpVersion, httpStatus);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpResponseStatus() {
        return httpStatus;
    }
}
