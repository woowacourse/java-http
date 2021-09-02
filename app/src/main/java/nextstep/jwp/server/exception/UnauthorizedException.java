package nextstep.jwp.server.exception;

import nextstep.jwp.server.http.response.HttpStatus;

public class UnauthorizedException extends HttpStatusException {

    private static final String MESSAGE = "권한이 없습니다.";

    public UnauthorizedException() {
        super(HttpStatus.getPath(HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
