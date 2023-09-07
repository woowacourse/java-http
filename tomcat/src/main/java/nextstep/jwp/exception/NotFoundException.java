package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class NotFoundException extends HttpGlobalException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
