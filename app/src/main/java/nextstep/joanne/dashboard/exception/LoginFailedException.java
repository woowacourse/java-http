package nextstep.joanne.dashboard.exception;

import nextstep.joanne.server.http.HttpStatus;

public class LoginFailedException extends HttpException {
    public LoginFailedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
