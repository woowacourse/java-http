package nextstep.jwp;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String account) {
        super("User가 존재하지 않습니다. id : " + account);
    }
}
