package nextstep.handler.exception;

public class InvalidQueryParameterException extends IllegalArgumentException {

    public InvalidQueryParameterException() {
        super("유효하지 않은 쿼리 파라미터입니다.");
    }
}
