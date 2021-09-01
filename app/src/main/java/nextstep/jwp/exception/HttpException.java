package nextstep.jwp.exception;

import nextstep.jwp.http.response.status.HttpStatus;

public class HttpException extends RuntimeException {

    private final HttpStatus httpStatus;

    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
