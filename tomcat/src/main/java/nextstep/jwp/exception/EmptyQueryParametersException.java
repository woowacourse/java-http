package nextstep.jwp.exception;

public class EmptyQueryParametersException extends RuntimeException {

    private static final String MESSAGE = "입력 값이 존재하지 않습니다.";

    public EmptyQueryParametersException() {
        super(MESSAGE);
    }
}
