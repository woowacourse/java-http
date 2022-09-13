package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.catalina.exception.InternalServerException;
import org.apache.coyote.ExceptionHandler;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class InternalServerExceptionHandler implements ExceptionHandler {

    @Override
    public boolean isResolvable(final Exception exception) {
        return exception instanceof InternalServerException;
    }

    @Override
    public void handle(Exception exception, Response response) throws IOException, URISyntaxException {
        log.error(exception.getMessage(), exception);
        response.redirect(HttpStatus.FOUND, "/500.html");
    }
}
