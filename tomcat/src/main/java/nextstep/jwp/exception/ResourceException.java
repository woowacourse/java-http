package nextstep.jwp.exception;

public class ResourceException extends BaseException {

    private final ResourceExceptionType exceptionType;

    public ResourceException(ResourceExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
