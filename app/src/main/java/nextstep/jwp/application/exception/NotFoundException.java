package nextstep.jwp.application.exception;

import nextstep.jwp.webserver.BaseException;

public class NotFoundException extends BaseException {

    public NotFoundException() {
        super(404);
    }
}
