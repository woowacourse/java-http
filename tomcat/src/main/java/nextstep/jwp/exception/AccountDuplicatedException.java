package nextstep.jwp.exception;

public class AccountDuplicatedException extends IllegalArgumentException {

    public AccountDuplicatedException(final String account) {
        super("해당 계정 이름은 중복됩니다 : " + account);
    }
}
