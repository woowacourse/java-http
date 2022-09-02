package nextstep.jwp.exception;

public class InvalidUserException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "유저 정보가 존재하지 않습니다.";

    public InvalidUserException() {
        super(EXCEPTION_MESSAGE);
    }
}
