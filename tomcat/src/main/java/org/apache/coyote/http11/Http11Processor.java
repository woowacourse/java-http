package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.global.ControllerDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser parser;
    private final HttpResponse response;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        parser = new HttpRequestParser();
        this.response = new HttpResponse();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = parser.extractRequest(inputStream);

            final Controller controller = ControllerDispatcher.dispatch(request.getMethod(), request.getPath());
            controller.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
