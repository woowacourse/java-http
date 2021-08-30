package nextstep.joanne.exception;

import nextstep.joanne.http.HttpStatus;

public class LoginFailedException extends HttpException {
    public LoginFailedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
