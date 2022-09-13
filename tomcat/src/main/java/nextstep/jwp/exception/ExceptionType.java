package nextstep.jwp.exception;

public enum ExceptionType {

    SERVER_EXCEPTION("서버 에러가 발생했습니다."),
    NOT_FOUND_USER_EXCEPTION("존재하지 않는 User 입니다."),
    MISS_MATCH_USER_PASSWORD_EXCEPTION("User 의 Password 가 일치하지 않습니다."),
    INVALID_HTTP_METHOD_TYPE_EXCEPTION("현재 지원하는 HTTP Method 형식이 아닙니다."),
    INVALID_HTTP_METHOD_EXCEPTION("잘못된 HTTP Method 형식입니다."),
    INVALID_HTTP_REGISTER_EXCEPTION("잘못된 회원가입 요청입니다."),
    INVALID_HTTP_LOGIN_EXCEPTION("잘못된 로그인 요청입니다."),
    INVALID_REQUEST_LINE_EXCEPTION("잘못된 REQUEST LINE 형식입니다."),
    INVALID_RESPONSE_HEADER_EXCEPTION("잘못된 RESPONSE HEADER 형식입니다."),
    INVALID_REQUEST_BODY_EXCEPTION("잘못된 REQUEST BODY 형식입니다."),
    INVALID_URL_EXCEPTION("잘못된 URL 형식입니다."),
    INVALID_SESSION_EXCEPTION("세션이 유효하지 않습니다."),
    NOT_FOUND_COOKIE_EXCEPTION("쿠키가 존재하지 않습니다."),
    INVALID_HANDLER_EXCEPTION("일치하는 Handler 를 찾을 수 없습니다."),
    INVALID_STATUS_CODE_EXCEPTION("잘못된 StatusCode 형식입니다."),
    INVALID_HTTP_VERSION_EXCEPTION("잘못된 HTTP Version 형식입니다."),
    INVALID_HTTP_HEADER_TYPE_EXCEPTION("잘못된 HttpHeaderType 형식입니다."),
    INVALID_CONTENT_TYPE_EXCEPTION("잘못된 Content type 형식입니다.");

    ExceptionType(String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return message;
    }
}
