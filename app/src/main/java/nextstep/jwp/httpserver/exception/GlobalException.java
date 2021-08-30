package nextstep.jwp.httpserver.exception;

import nextstep.jwp.httpserver.domain.StatusCode;

public class GlobalException extends RuntimeException {
    private final StatusCode statusCode;

    public GlobalException(String message, StatusCode code) {
        super(message);
        this.statusCode = code;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
