package nextstep.jwp.httpserver.exception;

import nextstep.jwp.httpserver.domain.response.StatusCode;

public class AuthorizationException extends GlobalException {

    public AuthorizationException(String message) {
        super(message, StatusCode.UNAUTHORIZED);
    }
}
