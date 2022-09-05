package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.frontController = new FrontController();
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

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpResponse httpResponse = frontController.performRequest(httpRequest);

            outputStream.write(httpResponse.toResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }
}
