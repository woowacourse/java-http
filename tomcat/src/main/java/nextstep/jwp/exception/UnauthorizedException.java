package nextstep.jwp.exception;

import org.apache.coyote.http11.response.HttpStatus;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
