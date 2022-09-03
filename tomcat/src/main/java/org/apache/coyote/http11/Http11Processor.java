package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
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

            String uri = reader.readLine().split(" ")[URI];
            if (uri.equals("/favicon.ico")) {
                return;
            }
            final ResponseHandler responseHandler = new ResponseHandler(uri);
            final String response = responseHandler.getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
