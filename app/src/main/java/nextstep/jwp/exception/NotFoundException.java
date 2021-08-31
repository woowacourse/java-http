package nextstep.jwp.exception;

public class NotFoundException extends RuntimeException {

    private static final String MESSAGE = "자원이 존재하지 않습니다.";

    public NotFoundException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
