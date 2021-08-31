package nextstep.jwp.web.exception;

public class InvalidHttpContentTypeException extends RuntimeException {

    public InvalidHttpContentTypeException() {
        super("Content-Type of the request is invalid.");
    }
}
