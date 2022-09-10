package nextstep.jwp.exception;

public class NotFoundResourceException extends NotFoundException {
    public NotFoundResourceException() {
        super("존재하지 않는 리소스입니다.");
    }
}
