package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.apache.coyote.http11.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    protected static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest request = HttpRequest.from(bufferedReader);
            final HttpResponse response = handle(request);

            log.info(request.getPath()); // thread 확인용 log

            outputStream.write(response.toResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(final HttpRequest request) throws IOException {
        final String path = request.getPath();
        final Controller controller = RequestMapping.fromPath(path);

        return controller.service(request);
    }
}
