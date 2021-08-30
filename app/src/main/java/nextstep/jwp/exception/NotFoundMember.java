package nextstep.jwp.exception;

public class NotFoundMember extends RuntimeException {
    public NotFoundMember() {
    }

    public NotFoundMember(String message) {
        super(message);
    }
}
