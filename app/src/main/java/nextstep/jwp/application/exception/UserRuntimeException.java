package nextstep.jwp.application.exception;

import nextstep.jwp.web.exception.ApplicationRuntimeException;

public class UserRuntimeException extends ApplicationRuntimeException {

    public UserRuntimeException(String message) {
        super(message);
    }
}
