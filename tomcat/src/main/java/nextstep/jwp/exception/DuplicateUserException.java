package nextstep.jwp.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException() {
        super("이미 존재하는 계정 입니다.");
    }
}
