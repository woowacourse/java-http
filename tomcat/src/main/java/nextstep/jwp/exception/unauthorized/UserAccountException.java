package nextstep.jwp.exception.unauthorized;

public class UserAccountException extends UnAuthorizedException {
    public UserAccountException(String message) {
        super(message);
    }
}
