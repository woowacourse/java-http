package nextstep.jwp.exception;

public class HttpMethodNotAllowedException extends RuntimeException {

    public HttpMethodNotAllowedException() {
    }

    public HttpMethodNotAllowedException(String message) {
        super(message);
    }
}
