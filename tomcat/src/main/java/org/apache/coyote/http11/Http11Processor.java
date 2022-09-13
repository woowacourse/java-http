package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.Application;
import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.exception.InternalServerException;
import org.apache.coyote.Container;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Socket connection;
    private final Container container;

    public Http11Processor(final Socket connection, final Container container) {
        this.connection = connection;
        this.container = container;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final Request request = Request.of(bufferedReader);
            final Response response = Response.of(outputStream);
            execute(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(final Request request, final Response response) throws Exception {
        try {
            container.findController(request)
                    .service(request, response);
        } catch (InternalServerException | NotFoundException e) {
            container.findExceptionHandler(e)
                    .handle(e, response);
        }
    }
}
