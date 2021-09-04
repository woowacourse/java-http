package nextstep.jwp.dashboard.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String account) {
        super(String.format("User does not exist. account : %s", account));
    }
}
