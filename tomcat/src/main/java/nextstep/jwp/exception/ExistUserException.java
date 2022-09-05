package nextstep.jwp.exception;

public class ExistUserException extends RuntimeException {

    public ExistUserException() {
        super("이미 존재하는 account로 회원가입할 수 없습니다.");
    }
}
