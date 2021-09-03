package nextstep.jwp.server.exception;

import nextstep.jwp.server.http.response.HttpStatus;

public class NotFoundException extends HttpStatusException {

    private static final String MESSAGE = "자원이 존재하지 않습니다.";

    public NotFoundException() {
        super(HttpStatus.getPath(HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND, MESSAGE);
    }
}
