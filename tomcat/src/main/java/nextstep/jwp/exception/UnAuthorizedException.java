package nextstep.jwp.exception;

public class UnAuthorizedException extends RuntimeException {

    private static final String ERROR_MESSAGE = "로그인에 실패했습니다. -> account: %s";

    public UnAuthorizedException(final String account) {
        super(String.format(ERROR_MESSAGE, account));
    }
}
