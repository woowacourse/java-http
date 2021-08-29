package nextstep.jwp.exception;

import nextstep.jwp.http.response.status.HttpStatus;

public class BadRequestException extends HttpException {

    public BadRequestException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
