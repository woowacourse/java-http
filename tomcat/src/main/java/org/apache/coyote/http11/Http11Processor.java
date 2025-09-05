package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.StaticFileHandler;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.handler.HttpResponseDispatcher;
import org.apache.coyote.http11.handler.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestHandler httpRequestHandler;
    private final Handler httpResponseDispatcher;

    public Http11Processor(
            final Socket connection,
            final HttpRequestHandler httpRequestHandler
    ) {
        this.connection = connection;
        this.httpRequestHandler = httpRequestHandler;
        this.httpResponseDispatcher = new HttpResponseDispatcher(List.of(new StaticFileHandler("static", "index.html")));
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

            HttpRequest httpRequest = httpRequestHandler.parse(inputStream);
            httpResponseDispatcher.handle(httpRequest, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            try {
                Responses.serverError(connection.getOutputStream(), "HTTP/1.1");
            } catch (IOException ignore) { /* 무시 */ }
        }
    }
}
