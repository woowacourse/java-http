package nextstep.jwp.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "유저를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
