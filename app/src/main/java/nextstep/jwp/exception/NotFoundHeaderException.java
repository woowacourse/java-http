package nextstep.jwp.exception;

public class NotFoundHeaderException extends RuntimeException {

    public NotFoundHeaderException(String header) {
        super("존재하지 않는 header name 입니다. header: " + header);
    }
}
