package nextstep.jwp.exception;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

import org.apache.coyote.http11.HttpStatus;

public enum FormDataExceptionType implements BaseExceptionType {
    
    INVALID_FORM(BAD_REQUEST, "데이터가 존재하지 않습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    FormDataExceptionType(HttpStatus httpStatus, String errorMessage) {
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
