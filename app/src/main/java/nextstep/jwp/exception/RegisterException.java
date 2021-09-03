package nextstep.jwp.exception;

public class RegisterException extends RuntimeException {

    private static final String MESSAGE = "파일을 찾을 수 없습니다.";

    public RegisterException() {
        super(MESSAGE);
    }
}
