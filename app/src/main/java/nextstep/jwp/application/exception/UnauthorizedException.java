package nextstep.jwp.application.exception;

import nextstep.jwp.webserver.BaseException;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(401);
    }
}
