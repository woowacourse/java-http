package org.apache.coyote.http11;

public class HttpResponseEntity {

    private final HttpStatus httpStatus;
    private final String path;

    public HttpResponseEntity(final HttpStatus httpStatus, final String path) {
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }
}
