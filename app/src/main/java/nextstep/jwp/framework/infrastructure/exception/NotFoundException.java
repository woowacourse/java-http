package nextstep.jwp.framework.infrastructure.exception;

import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class NotFoundException extends RuntimeException {

    private final HttpStatus httpStatus;

    public NotFoundException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
