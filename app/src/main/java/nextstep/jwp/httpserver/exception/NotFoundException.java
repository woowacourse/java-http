package nextstep.jwp.httpserver.exception;

import nextstep.jwp.httpserver.domain.StatusCode;

public class NotFoundException extends GlobalException {
    public NotFoundException(String message) {
        super(message, StatusCode.NOT_FOUND);
    }
}
