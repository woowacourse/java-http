package nextstep.jwp.exception;

public class BadRequestException extends CustomException {

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }
}
