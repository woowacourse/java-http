package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.exception.UncheckedServletException;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HandlerMapper;
import org.apache.coyote.model.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final var httpRequest = HttpRequest.of(reader);
            final var httpResponse = createResponse(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final HttpRequest httpRequest) {
        final Handler handler = HandlerMapper.findHandler(httpRequest);
        return handler.getResponse();
    }
}
