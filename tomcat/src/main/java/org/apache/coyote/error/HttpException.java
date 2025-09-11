package org.apache.coyote.error;

import org.apache.coyote.httpResponse.StatusCode;

public class HttpException extends RuntimeException {

    private final ErrorCode errorCode;

    public HttpException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public StatusCode getStatusCode() {
        return errorCode.getStatusCode();
    }
}
