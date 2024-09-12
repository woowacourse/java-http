package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatusCode;

public class RequestException extends RuntimeException {
    public RequestException(HttpStatusCode status, String message) {
        ErrorResponseHandler.getInstance()
                .handleError(status, message);
    }
}
