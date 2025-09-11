package org.apache.coyote.http11.processor;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceHandler resourceHandler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceHandler = new ResourceHandler();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var httpRequest = HttpRequestParser.parse(inputStream);
            final var httpResponse = new HttpResponse();

            final var session = SessionManager.resolveSession(httpRequest, httpResponse);

            httpRequest.setSession(session);

            resourceHandler.execute(httpRequest, httpResponse);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
