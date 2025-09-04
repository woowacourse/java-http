package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dto.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpResponseHandler httpResponseHandler;
    private final HttpRequestHandler httpRequestHandler;

    public Http11Processor(
            final Socket connection,
            final HttpResponseHandler httpResponseHandler,
            final HttpRequestHandler httpRequestHandler
    ) {
        this.connection = connection;
        this.httpResponseHandler = httpResponseHandler;
        this.httpRequestHandler = httpRequestHandler;
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

            HttpRequest httpRequest = httpRequestHandler.process(inputStream);
            httpResponseHandler.process(httpRequest, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
