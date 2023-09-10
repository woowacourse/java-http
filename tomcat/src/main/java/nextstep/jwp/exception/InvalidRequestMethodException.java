package nextstep.jwp.exception;

import org.apache.coyote.http11.common.HttpStatus;

public class InvalidRequestMethodException extends HttpGlobalException {

    public InvalidRequestMethodException(String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
