package nextstep.jwp.exception;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException() {
        super("중복된 계정입니다.");
    }
}
