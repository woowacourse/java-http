package nextstep.jwp.exception;

public class DuplicatedAccountException extends RuntimeException {

    public DuplicatedAccountException(final String account) {
        super("이미 존재하는 Acoount 입니다. 입력하신 Account : " + account);
    }
}
