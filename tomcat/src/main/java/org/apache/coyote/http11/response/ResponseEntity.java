package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.ServletResponseEntity;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String mimeType;
    private final String body;

    public ResponseEntity(final HttpStatus httpStatus, final String mimeType, final String body) {
        this.httpStatus = httpStatus;
        this.mimeType = mimeType;
        this.body = body;
    }

    public static ResponseEntity of(final ServletResponseEntity response) {
        return new ResponseEntity(response.getHttpStatus(), response.getMimeType(), response.getBody());
    }

    public static ResponseEntity createHtmlResponse(final ServletResponseEntity response) {
        return new ResponseEntity(response.getHttpStatus(), "text/html", response.getResource());
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
