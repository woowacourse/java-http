package nextstep.jwp.exception;

import nextstep.jwp.http.common.HttpStatus;

public class UnauthorizedException extends HttpGlobalException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
