package nextstep.jwp.exception;

public class HttpMethodNotAllowedException extends RuntimeException {

    private static final String MESSAGE = "유효하지 않은 HTTP method 입니다.";

    public HttpMethodNotAllowedException() {
        super(MESSAGE);
    }
}
