package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String mimeType;
    private final String body;

    public ResponseEntity(final HttpStatus httpStatus, final String mimeType, final String body) {
        this.httpStatus = httpStatus;
        this.mimeType = mimeType;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getBody() {
        return body;
    }
}
