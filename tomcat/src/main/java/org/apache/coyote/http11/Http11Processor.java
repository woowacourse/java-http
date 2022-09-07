package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.ControllerContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.ExceptionHandler;
import org.apache.coyote.exception.InternalServerException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_RESOURCE = "/login.html";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final Request request = Request.of(inputStream);
            final Response response = Response.of(outputStream);
            execute(request, response);
        } catch (IOException | UncheckedServletException | IllegalArgumentException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(final Request request, final Response response) throws IOException, URISyntaxException {
        try {
            final Controller controller = ControllerContainer.findController(request);
            controller.run(request, response);
        } catch (InternalServerException | NotFoundException e) {
            ExceptionHandler.handle(e, response);
        }
    }
}
