package nextstep.joanne.exception;

import nextstep.joanne.http.HttpStatus;

public class FileNotFoundException extends HttpException {
    public FileNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
