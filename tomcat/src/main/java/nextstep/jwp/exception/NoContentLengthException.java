package nextstep.jwp.exception;

public class NoContentLengthException extends RuntimeException {
    public NoContentLengthException() {
        super("Content-Length 가 존재하지 않습니다.");
    }
}
