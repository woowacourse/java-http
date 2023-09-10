package nextstep.jwp.exception;

public class NotFoundHandlerException extends RuntimeException {

    public NotFoundHandlerException() {
        super("Match Handler Not Found");
    }
}
