package nextstep.jwp.application.exception;

public class AlreadyExistsAccountException extends IllegalArgumentException {

    public AlreadyExistsAccountException() {
        super("이미 존재하는 아이디입니다.");
    }
}
