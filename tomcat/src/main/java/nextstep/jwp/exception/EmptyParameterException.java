package nextstep.jwp.exception;

public class EmptyParameterException extends RuntimeException {

    private static final String MESSAGE = "입력 값이 누락되었습니다.";

    public EmptyParameterException() {
        super(MESSAGE);
    }
}
