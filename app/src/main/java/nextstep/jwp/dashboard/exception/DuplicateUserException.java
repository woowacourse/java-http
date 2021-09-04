package nextstep.jwp.dashboard.exception;

public class DuplicateUserException extends BadRequestException {
    public DuplicateUserException(String account) {
        super(String.format("Duplicated user information. account : %s", account));
    }
}
