package nextstep.jwp.framework.infrastructure.exception;

import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class WebServerException extends RuntimeException {

    private final HttpStatus httpStatus;

    public WebServerException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
