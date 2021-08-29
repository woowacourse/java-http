package nextstep.jwp.web.exception;

public class InvalidHttpMethodException extends RuntimeException {

    public InvalidHttpMethodException(String name) {
        super("Invalid http method : " + name);
    }
}
