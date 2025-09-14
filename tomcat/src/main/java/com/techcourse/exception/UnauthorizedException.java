package com.techcourse.exception;

import org.apache.catalina.exception.Http4xxException;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class UnauthorizedException extends Http4xxException {

    public UnauthorizedException(Http11Response response) {
        super("Unauthorized", response, HttpStatus.UNAUTHORIZED);
    }
}
