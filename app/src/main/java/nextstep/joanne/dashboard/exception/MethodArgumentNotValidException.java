package nextstep.joanne.dashboard.exception;

import nextstep.joanne.server.http.HttpStatus;

public class MethodArgumentNotValidException extends HttpException {
    public MethodArgumentNotValidException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public MethodArgumentNotValidException(HttpStatus httpStatus) {
        super(httpStatus);
    }
}
