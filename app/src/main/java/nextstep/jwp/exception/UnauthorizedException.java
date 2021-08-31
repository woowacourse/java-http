package nextstep.jwp.exception;

import nextstep.jwp.http.response.status.HttpStatus;

public class UnauthorizedException extends HttpException {

    public UnauthorizedException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
