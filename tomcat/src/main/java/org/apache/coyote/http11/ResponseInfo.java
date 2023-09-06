package org.apache.coyote.http11;

public class ResponseInfo {

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;

    private ResponseInfo(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static ResponseInfo defaultResponse() {
        return new ResponseInfo(HttpVersion.HTTP_1_1, HttpStatus.OK);
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void updateResponseInfo(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }
}
