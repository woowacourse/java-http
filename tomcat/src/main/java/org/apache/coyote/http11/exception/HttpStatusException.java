package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class HttpStatusException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public HttpStatusException(final HttpStatusCode statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
