package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestHandler requestHandler;

    public Http11Processor(final Socket connection, RequestHandler requestHandler) {
        this.connection = connection;
        this.requestHandler = requestHandler;
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

            final Optional<HttpRequest> request = new HttpRequestReader(inputStream).read();
            if (request.isEmpty()) {
                return;
            }
            final HttpResponse response = requestHandler.handle(request.get());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException | HttpRequestParseException e) {
            log.error(e.getMessage(), e);
        }
    }
}
