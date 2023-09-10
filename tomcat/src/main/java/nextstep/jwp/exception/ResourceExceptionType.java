package nextstep.jwp.exception;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

import org.apache.coyote.http11.HttpStatus;

public enum ResourceExceptionType implements BaseExceptionType {

    RESOURCE_NOT_FOUND(BAD_REQUEST, "요청받은 리소스가 존재하지 않습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    ResourceExceptionType(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}
