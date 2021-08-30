package nextstep.joanne.exception;

import nextstep.joanne.http.HttpStatus;

public class FileErrorException extends HttpException {
    public FileErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
