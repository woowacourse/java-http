package nextstep.jwp;

public class UserNotFoundException extends DashboardException {
    public UserNotFoundException(String account) {
        super(String.format("User does not exist. account : %s", account));
    }
}
