package nextstep.jwp.exception;

public class DuplicatedUserException extends RuntimeException {

    private static final String ERROR_MESSAGE = "이미 존재하는 계정입니다 -> account: %s";

    public DuplicatedUserException(final String account) {
        super(String.format(ERROR_MESSAGE, account));
    }
}
