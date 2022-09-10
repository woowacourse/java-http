package nextstep.jwp.exception;

public class DuplicateAccountException extends BadRequestException {

    public DuplicateAccountException() {
        super("이미 존재하는 계정입니다.");
    }
}
