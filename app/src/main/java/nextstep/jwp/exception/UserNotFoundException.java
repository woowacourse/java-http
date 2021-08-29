package nextstep.jwp.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String MESSAGE = "유저가 존재하지 않습니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
