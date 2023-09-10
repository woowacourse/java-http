package nextstep.jwp.exception;

import org.apache.coyote.http11.common.HttpStatus;

public class BadRequestException extends HttpGlobalException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
