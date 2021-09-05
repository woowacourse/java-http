package nextstep.jwp.web.exception;

public class InputException extends RuntimeException {
    public InputException(String source) {
        super(String.format("Error while reading input from %s.", source));
    }
}
