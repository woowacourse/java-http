package mvc.controller.mapping.exception;

public class UnsupportedHttpRequestException extends IllegalArgumentException {

    public UnsupportedHttpRequestException() {
        super("해당 요청을 처리할 수 없습니다.");
    }
}
