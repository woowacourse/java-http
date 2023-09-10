package nextstep.jwp.exception;

public class AuthException extends BaseException {

    private final AuthExceptionType exceptionType;

    public AuthException(AuthExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
