package org.apache.coyote.exception;

import org.apache.coyote.response.Status;

public class HttpException extends RuntimeException {

    protected Status status;
    protected String message;

    public Status getStatus() {
        return status;
    }
}
