package nextstep.jwp.exception;

public class UncheckedServletException extends RuntimeException {

    private static final String MESSAGE = "유효하지 않은 요청입니다.";

    public UncheckedServletException() {
        super(MESSAGE);
    }
}
