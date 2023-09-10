package nextstep.jwp.exception;

public class InvalidQueryStringException extends RuntimeException {

    public InvalidQueryStringException() {
        super("쿼리 스트링은 key=value 형식이어야 합니다.");
    }
}
