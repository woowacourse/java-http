package nextstep.joanne.exception;

import nextstep.joanne.http.HttpStatus;

public class PageNotFoundException extends HttpException {
    public PageNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
