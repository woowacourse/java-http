package nextstep.jwp.httpserver.exception;

import nextstep.jwp.httpserver.domain.StatusCode;

public class AuthorizationException extends GlobalException {
    private StatusCode statusCode;

    public AuthorizationException(String message) {
        super(message, StatusCode.UNAUTHORIZED);
    }
}
