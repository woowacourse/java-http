package nextstep.jwp.exception;

public class InternalServerErrorException extends CustomResponseException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
