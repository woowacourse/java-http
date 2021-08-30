package nextstep.jwp.httpserver.exception;

import nextstep.jwp.httpserver.domain.StatusCode;

public class AuthorizationException extends GlobalException {

    public AuthorizationException(String message) {
        super(message, StatusCode.UNAUTHORIZED);
    }
}
