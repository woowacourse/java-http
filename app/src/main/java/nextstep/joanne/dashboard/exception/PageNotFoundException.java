package nextstep.joanne.dashboard.exception;

import nextstep.joanne.server.http.HttpStatus;

public class PageNotFoundException extends HttpException {
    public PageNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
