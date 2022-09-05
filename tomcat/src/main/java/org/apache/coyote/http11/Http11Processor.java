package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.presentation.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);
    private static final int URI = 1;

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
            final String startLine = reader.readLine();
            final Controller handler = RequestHandlerMapping.getHandler(startLine);

            final ResponseEntity responseEntity = ResponseHandler.handle(handler, startLine);
            outputStream.write(responseEntity.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
