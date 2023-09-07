package nextstep.jwp.exception;

import nextstep.jwp.http.common.HttpStatus;

public class BadRequestException extends HttpGlobalException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
