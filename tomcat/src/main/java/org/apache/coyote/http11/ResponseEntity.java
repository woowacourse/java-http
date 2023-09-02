package org.apache.coyote.http11;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String uri;

    public ResponseEntity(final HttpStatus httpStatus, final String uri) {
        this.httpStatus = httpStatus;
        this.uri = uri;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getUri() {
        return uri;
    }
}
