package nextstep.jwp.exception;

import org.apache.coyote.http11.common.HttpStatus;

public class NotFoundException extends HttpGlobalException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
