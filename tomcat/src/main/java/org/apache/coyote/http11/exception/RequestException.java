package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatusCode;

public class RequestException extends IllegalArgumentException {

    private final HttpStatusCode statusCode;
    private final String errorPagePath;

    public RequestException(HttpStatusCode statusCode, String errorPagePath) {
        this.statusCode = statusCode;
        this.errorPagePath = errorPagePath;
    }

    public void handleErrorResponse() {
        ErrorResponseHandler.getInstance()
                .handleErrorMessage(statusCode, errorPagePath);
    }
}
