package nextstep.jwp.exception;

import nextstep.jwp.web.http.response.StatusCode;

public class InvalidUserException extends HttpException {

    public static final StatusCode CODE = StatusCode.UNAUTHORIZED;

    public InvalidUserException() {
        super(CODE);
    }
}
