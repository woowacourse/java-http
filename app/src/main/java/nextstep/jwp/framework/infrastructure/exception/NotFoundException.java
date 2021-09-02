package nextstep.jwp.framework.infrastructure.exception;

import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class NotFoundException extends WebServerException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
