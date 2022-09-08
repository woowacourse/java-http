package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.catalina.exception.InternalServerException;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class InternalServerExceptionController implements org.apache.coyote.ExceptionController {
    @Override
    public boolean isResolvable(final Exception exception) {
        return exception instanceof InternalServerException;
    }

    @Override
    public void handle(Exception exception, Response response) throws IOException, URISyntaxException {
        log.error(exception.getMessage(), exception);
        response.addHeader("Location", "/500.html");
        response.write(HttpStatus.FOUND);
    }
}
