package org.apache.catalina.exception;

import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class Http4xxException extends RuntimeException {

    public Http4xxException(String message, Http11Response response, HttpStatus httpStatus) {
        super(message);
        response.setState(httpStatus);
    }
}
