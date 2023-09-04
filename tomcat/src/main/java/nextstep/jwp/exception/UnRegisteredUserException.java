package nextstep.jwp.exception;

public class UnRegisteredUserException extends RuntimeException {

    public UnRegisteredUserException() {
        super("가입되지 않은 사용자입니다.");
    }
}
