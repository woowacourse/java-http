package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.startline.HttpStatusCode;

public class HttpStatusException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public HttpStatusException(final HttpStatusCode statusCode) {
        super("http 응답 오류");
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
