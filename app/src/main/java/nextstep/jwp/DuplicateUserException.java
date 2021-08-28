package nextstep.jwp;

public class DuplicateUserException extends DashboardException {
    public DuplicateUserException(String account) {
        super(String.format("Duplicated user information. account : %s", account));
    }
}
