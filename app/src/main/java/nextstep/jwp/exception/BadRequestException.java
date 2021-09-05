package nextstep.jwp.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("잘못된 요청입니다.");
    }
}
