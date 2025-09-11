package org.apache.catalina.exception;

import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class Http4xxException extends RuntimeException {

    public Http4xxException() {
        super("잘못된 요청입니다.");
    }

    public Http4xxException(String message, Http11Response response, HttpStatus httpStatus) {
        super(message);
        response.setState(httpStatus);
    }

    public Http4xxException(Exception e, Http11Response response, HttpStatus httpStatus) {
        super(e.getMessage());
        response.setState(httpStatus);
    }

    public Http4xxException(Http11Response response, HttpStatus httpStatus) {
        super(httpStatus.toString());
        response.setState(httpStatus);
    }
}
