package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.ExceptionHandler;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class NotFoundExceptionHandler implements ExceptionHandler {

    @Override
    public boolean isResolvable(final Exception exception) {
        return exception instanceof NotFoundException;
    }

    @Override
    public void handle(final Exception exception, final Response response) throws IOException, URISyntaxException {
        log.error(exception.getMessage(), exception);
        response.redirect(HttpStatus.FOUND, "/404.html");
    }
}
