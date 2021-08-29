package nextstep.jwp.exception;

import nextstep.jwp.http.response.status.HttpStatus;

public class InternalServerException extends HttpException {

    public InternalServerException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
