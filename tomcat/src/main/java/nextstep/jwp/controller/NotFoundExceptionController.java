package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.ExceptionController;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class NotFoundExceptionController implements ExceptionController {
    @Override
    public boolean isResolvable(final Exception exception) {
        return exception instanceof NotFoundException;
    }

    @Override
    public void handle(final Exception exception, final Response response) throws IOException, URISyntaxException {
        log.error(exception.getMessage(), exception);
        response.addHeader("Location", "/404.html");
        response.write(HttpStatus.FOUND);
    }
}
