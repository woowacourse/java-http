package mvc.controller.exception;

public class UnsupportedHttpMethodException extends IllegalArgumentException {

    public UnsupportedHttpMethodException() {
        super("해당 요청을 처리할 수 없습니다.");
    }
}
