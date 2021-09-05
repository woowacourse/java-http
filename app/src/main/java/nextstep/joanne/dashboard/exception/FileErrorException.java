package nextstep.joanne.dashboard.exception;

import nextstep.joanne.server.http.HttpStatus;

public class FileErrorException extends HttpException {
    public FileErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
