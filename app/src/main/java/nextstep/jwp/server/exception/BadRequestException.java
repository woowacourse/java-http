package nextstep.jwp.server.exception;

public class BadRequestException extends RuntimeException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public BadRequestException() {
        super(MESSAGE);
    }
}
