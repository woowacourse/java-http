package mvc.controller.exception;

public class InvalidParameterException extends IllegalArgumentException {

    public InvalidParameterException() {
        super("유효하지 않은 쿼리 파라미터입니다.");
    }
}
