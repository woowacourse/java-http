package nextstep.jwp.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "존재하지 않는 계정입니다. -> account: %s";

    public UserNotFoundException(String account) {
        super(String.format(ERROR_MESSAGE, account));
    }
}
