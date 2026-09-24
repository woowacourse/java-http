package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpStatus;

public class HttpException extends Exception {

    public HttpException(HttpStatus status) {
        super(status.status());
    }
}
