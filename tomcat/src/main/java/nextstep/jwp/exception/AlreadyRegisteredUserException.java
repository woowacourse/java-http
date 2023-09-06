package nextstep.jwp.exception;

public class AlreadyRegisteredUserException extends RuntimeException {

    public AlreadyRegisteredUserException() {
        super("이미 가입된 유저입니다.");
    }
}
