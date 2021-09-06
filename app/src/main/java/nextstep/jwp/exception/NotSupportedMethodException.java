package nextstep.jwp.exception;

public class NotSupportedMethodException extends RuntimeException {

    public NotSupportedMethodException() {
        super("지원하지 않는 HTTP 요청입니다.");
    }
}
