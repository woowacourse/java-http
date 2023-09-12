package nextstep.jwp.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("비밀번호가 틀렸습니다.");
    }
}
