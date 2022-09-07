package nextstep.jwp.exception;

public class UnsupportedMethodException extends RuntimeException {

    private static final String MESSAGE = "처리할 수 없는 요청입니다.";

    public UnsupportedMethodException() {
        super(MESSAGE);
    }
}
