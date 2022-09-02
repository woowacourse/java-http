package nextstep.jwp.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("회원이 존재하지 않습니다.");
    }
}
