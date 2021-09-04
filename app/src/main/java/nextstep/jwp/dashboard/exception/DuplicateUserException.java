package nextstep.jwp.dashboard.exception;

import nextstep.jwp.web.exception.BadRequestException;

public class DuplicateUserException extends BadRequestException {
    public DuplicateUserException(String account) {
        super(String.format("Duplicated user information. account : %s", account));
    }
}
