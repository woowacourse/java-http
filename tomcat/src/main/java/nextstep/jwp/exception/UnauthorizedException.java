package nextstep.jwp.exception;

import org.apache.coyote.http11.common.HttpStatus;

public class UnauthorizedException extends HttpGlobalException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
