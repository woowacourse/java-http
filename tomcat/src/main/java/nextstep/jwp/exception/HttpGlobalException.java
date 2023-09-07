package nextstep.jwp.exception;

import nextstep.jwp.http.common.HttpStatus;

public abstract class HttpGlobalException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    protected HttpGlobalException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
