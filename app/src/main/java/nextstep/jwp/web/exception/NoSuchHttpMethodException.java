package nextstep.jwp.web.exception;

public class NoSuchHttpMethodException extends RuntimeException {
    private static final String MESSAGE = "유효한 HTTP 메소드가 아닙니다.";

    public NoSuchHttpMethodException() {
        super(MESSAGE);
    }
}
