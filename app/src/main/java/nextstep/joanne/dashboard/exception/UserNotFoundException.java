package nextstep.joanne.dashboard.exception;

import nextstep.joanne.server.http.HttpStatus;

public class UserNotFoundException extends HttpException {
    public UserNotFoundException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
