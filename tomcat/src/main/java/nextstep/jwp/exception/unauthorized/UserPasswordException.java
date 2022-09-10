package nextstep.jwp.exception.unauthorized;

public class UserPasswordException extends UnAuthorizedException {

    public UserPasswordException(String message) {
        super(message);
    }
}
