package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class UnauthorizedException extends HttpGlobalException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
