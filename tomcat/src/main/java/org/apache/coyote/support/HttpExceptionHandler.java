package org.apache.coyote.support;

import org.apache.exception.HttpException;

public class HttpExceptionHandler {

    public String handle(HttpException exception) {
        if (exception.hasErrorStatus(HttpStatus.NOT_FOUND)) {
            return ResourceResponse.ofNotFound().toHttpResponseMessage();
        }
        return ResourceResponse.ofInternalServerError().toHttpResponseMessage();
    }
}
