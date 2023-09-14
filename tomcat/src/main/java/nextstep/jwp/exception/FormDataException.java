package nextstep.jwp.exception;

public class FormDataException extends BaseException {

    private final FormDataExceptionType exceptionType;

    public FormDataException(FormDataExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
