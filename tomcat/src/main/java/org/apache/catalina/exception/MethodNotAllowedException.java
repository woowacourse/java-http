package org.apache.catalina.exception;

import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class MethodNotAllowedException extends Http4xxException {

    public MethodNotAllowedException(Http11Response response) {
        super("Method Not Allowed", response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    public MethodNotAllowedException(Exception e, Http11Response response) {
        super(e.getMessage(), response, HttpStatus.METHOD_NOT_ALLOWED);

    }
}
