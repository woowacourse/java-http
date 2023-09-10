package nextstep.jwp.exception;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

import org.apache.coyote.http11.HttpStatus;

public enum AuthExceptionType implements BaseExceptionType {

    INVALID_SESSION_ID(UNAUTHORIZED, "잘못된 세션 아이디입니다"),
    USER_NO_EXIST_IN_SESSION(UNAUTHORIZED, "세션에 회원정보가 존재하지 않습니다"),
    INVALID_ID_OR_PASSWORD(UNAUTHORIZED, "아이디나 비밀번호를 확인해주세요"),
    DUPLICATED_ID(BAD_REQUEST, "이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요"),
    INVALID_FORM(BAD_REQUEST, "데이터가 존재하지 않습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    AuthExceptionType(HttpStatus httpStatus, String errorMessage) {
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
