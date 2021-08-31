package nextstep.jwp.mvc.exception;

import nextstep.jwp.core.exception.StatusException;
import nextstep.jwp.webserver.response.StatusCode;

public class PageNotFoundException extends StatusException {

    public PageNotFoundException() {
        super(StatusCode.NOT_FOUND, "404.html");
    }
}
