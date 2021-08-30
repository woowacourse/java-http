package nextstep.joanne.exception;

import nextstep.joanne.http.HttpStatus;

public class UserNotFoundException extends HttpException {
    public UserNotFoundException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
