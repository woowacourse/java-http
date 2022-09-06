package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.presentation.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.handler.RequestHandlerMapping;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);

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

            final HttpHeader httpHeader = getHeaders(reader, startLine);
            final HttpBody httpBody = getBody(reader);
            final ResponseEntity responseEntity = RequestHandler.handle(handler, httpHeader, httpBody);
            outputStream.write(responseEntity.getResponse(httpHeader).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private HttpHeader getHeaders(final BufferedReader reader, final String startLine) throws IOException {
        StringBuilder headers = new StringBuilder();
        String header;
        while ((header = reader.readLine()) != null && header.length() != 0) {
            headers.append(header).append("\n");
        }
        return new HttpHeader(startLine, headers.toString());
    }

    private HttpBody getBody(final BufferedReader reader) throws IOException {
        StringBuilder payload = new StringBuilder();
        while(reader.ready()) {
            payload.append((char) reader.read());
        }
        return new HttpBody(payload.toString());
    }
}
