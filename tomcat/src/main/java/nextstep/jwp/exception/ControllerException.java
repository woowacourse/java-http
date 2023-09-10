package nextstep.jwp.exception;

public class ControllerException extends BaseException {

    private final ControllerExceptionType exceptionType;

    public ControllerException(ControllerExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
