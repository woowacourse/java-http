package org.apache.coyote.http11;

import java.net.Socket;
import nextstep.Application;
import nextstep.jwp.exception.NotFoundException;
import org.apache.catalina.exception.InternalServerException;
import org.apache.coyote.ControllerFinder;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Socket connection;
    private final ControllerFinder controllerFinder;

    public Http11Processor(final Socket connection, final ControllerFinder controllerFinder) {
        this.connection = connection;
        this.controllerFinder = controllerFinder;
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(final Request request, final Response response) throws Exception {
        try {
            controllerFinder.findController(request)
                    .service(request, response);
        } catch (InternalServerException | NotFoundException e) {
            controllerFinder.findExceptionHandler(e)
                    .handle(e, response);
        }
    }
}
