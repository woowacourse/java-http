package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public abstract class AbstractHttpException extends RuntimeException implements HttpException {

    public AbstractHttpException() {
    }

    public AbstractHttpException(String message) {
        super(message);
    }

    public abstract HttpStatus httpStatus();
}
