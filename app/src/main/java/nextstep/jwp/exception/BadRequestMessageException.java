package nextstep.jwp.exception;

public class BadRequestMessageException extends RuntimeException {

    private static final String MESSAGE = "{\"message\": \"잘못된 request message 입니다.\"}";

    public BadRequestMessageException() {
        super(MESSAGE);
    }
}
