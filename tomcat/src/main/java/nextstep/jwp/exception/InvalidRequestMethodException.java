package nextstep.jwp.exception;

import nextstep.jwp.http.common.HttpStatus;

public class InvalidRequestMethodException extends HttpGlobalException {

    public InvalidRequestMethodException(String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
