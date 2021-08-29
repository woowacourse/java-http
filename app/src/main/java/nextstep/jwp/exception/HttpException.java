package nextstep.jwp.exception;

import nextstep.jwp.web.http.response.StatusCode;

public class HttpException extends RuntimeException {

    private final StatusCode statusCode;

    public HttpException() {
        this(StatusCode.INTERNAL_SERVER_ERROR);
    }

    protected HttpException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return this.statusCode;
    }
}
