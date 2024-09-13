package org.apache.catalina.exception;

import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class TomcatException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    protected TomcatException(HttpStatusCode httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
