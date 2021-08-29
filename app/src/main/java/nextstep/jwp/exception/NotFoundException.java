package nextstep.jwp.exception;

public class NotFoundException extends RuntimeException {
    private static final String MSG = "Not Found - 404";

    public NotFoundException() {
        super(MSG);
    }
}
