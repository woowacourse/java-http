package nextstep.jwp.exception;

public class InvalidLoginException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "로그인 정보가 올바르지 않습니다.";

    public InvalidLoginException() {
        super(EXCEPTION_MESSAGE);
    }
}
