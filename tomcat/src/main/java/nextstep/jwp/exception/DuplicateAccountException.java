package nextstep.jwp.exception;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException() {
        super("이미 존재하는 계정입니다.");
    }
}
