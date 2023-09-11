package nextstep.jwp.exception;

public class UnsupportedMethodException extends RuntimeException {
    private static final String MESSAGE = "지원하지 않는 메서드입니다.";

    public UnsupportedMethodException() {
        super(MESSAGE);
    }
}
