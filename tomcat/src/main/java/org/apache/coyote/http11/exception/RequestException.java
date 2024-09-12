package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatusCode;

public class RequestException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String message;

    public RequestException(HttpStatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void handleErrorResponse() {
        ErrorResponseHandler.getInstance()
                .handleError(statusCode, message);
    }
}
