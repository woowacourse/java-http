package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import nextstep.jwp.RequestProcessor;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final HttpRequest httpRequest = HttpRequest.of(inputStream);
            final RequestProcessor requestProcessor = new RequestProcessor();
            final HttpResponse httpResponse = HttpResponse.from(requestProcessor.from(httpRequest));

            outputStream.write(httpResponse.toResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
