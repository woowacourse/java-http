package nextstep.joanne.dashboard.exception;

import nextstep.joanne.server.http.HttpStatus;

public class FileNotFoundException extends HttpException {
    public FileNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
