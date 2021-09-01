package nextstep.jwp.exception;

import nextstep.jwp.ui.response.HttpStatus;

public class InternalServerException extends HttpStatusException {

    private static final String MESSAGE = "알 수 없는 에러";

    public InternalServerException() {
        super(HttpStatus.getPath(HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
