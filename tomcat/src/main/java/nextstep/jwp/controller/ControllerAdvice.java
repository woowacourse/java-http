package nextstep.jwp.controller;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.BAD_REQUEST;
import static org.apache.coyote.response.StatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.response.StatusCode.NOT_FOUND;
import static org.apache.coyote.response.StatusCode.UNAUTHORIZED;

import javassist.NotFoundException;
import nextstep.jwp.exception.ExistUserException;
import nextstep.jwp.exception.NoSuchUserException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

    private ControllerAdvice() {
    }

    public static void handle(final HttpResponse response, final Exception exception) {
        if (exception != null) {
            log.error("Internal Server Error : {}", exception.getMessage());
            response.setResponse(INTERNAL_SERVER_ERROR, HTML, Location.from("/500.html"));
        }
        if (exception instanceof NoSuchUserException) {
            log.error("Unauthorized : {}", exception.getMessage());
            response.setResponse(UNAUTHORIZED, HTML, Location.from("/401.html"));
        }
        if (exception instanceof IllegalArgumentException || exception instanceof ExistUserException) {
            log.error("Bad Request : {}", exception.getMessage());
            response.setResponse(BAD_REQUEST, HTML, Location.from("/400.html"));
        }
        if (exception instanceof NotFoundException) {
            log.error("Not Found : {}", exception.getMessage());
            response.setResponse(NOT_FOUND, HTML, Location.from("/404.html"));
        }

        response.print();
    }
}
