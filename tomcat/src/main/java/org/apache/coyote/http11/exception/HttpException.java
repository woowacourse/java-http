package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.HttpStatus;

public class HttpException extends Exception {

    public HttpException(HttpStatus status) {
        super(status.status());
    }
}
