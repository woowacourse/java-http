package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.HttpParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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
            final HttpResponseProcessor responseProcessor = new HttpResponseProcessor(outputStream);
            try {
                final HttpRequest httpRequest = new Http11RequestProcessor(inputStream).process();
                responseProcessor.send(httpRequest.getUri());
            } catch (HttpParseException | URISyntaxException e) {
                log.warn("잘못된 HTTP 요청입니다.");
                responseProcessor.sendError(HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
