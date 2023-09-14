package nextstep.jwp.exception;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

import org.apache.coyote.http11.HttpStatus;

public enum ControllerExceptionType implements BaseExceptionType {

    UNSUPPORTED_REQUEST(BAD_REQUEST, "지원되지 않는 요청입니다"),
    UNSUPPORTED_METHOD(BAD_REQUEST, "존재하지 않는 메서드입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    ControllerExceptionType(HttpStatus httpStatus, String errorMessage) {
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
