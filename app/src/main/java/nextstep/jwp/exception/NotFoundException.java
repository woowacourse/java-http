package nextstep.jwp.exception;

import nextstep.jwp.http.response.status.HttpStatus;

public class NotFoundException extends HttpException {


    public NotFoundException(HttpStatus httpStatus,
            String message) {
        super(httpStatus, message);
    }
}
