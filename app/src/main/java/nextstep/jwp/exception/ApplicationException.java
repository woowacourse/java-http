package nextstep.jwp.exception;

public class ApplicationException extends RuntimeException {

    private final String HttpStatus;

    public ApplicationException(String message, String httpStatus) {
        super(message);
        this.HttpStatus = httpStatus;
    }

    public String getHttpStatus() {
        return HttpStatus;
    }
}
