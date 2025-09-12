package com.techcourse.exception;

public enum ErrorMessage {
    ACCOUNT_NOT_FOUND("그런 계정은 없다."),
    INVALID_PASSWORD("비밀번호가 안 맞아."),
    INVALID_REQUEST_LINE("요청 라인이 없다."),
    INVALID_HTTP_REQUEST_FORMAT("HTTP 요청 포맷이 아니다."),
    INVALID_QUERY_STRING("? 가 없잖아."),
    INVALID_LOGIN_REQUEST("로그인 요청이 이상하다."),
    INVALID_REGISTER_REQUEST("회원가입 요청이 이상하다."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
