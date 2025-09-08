package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http.handler.RequestHandler;
import org.apache.coyote.http.request.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final RequestHandler requestHandler;
    private final HttpRequestParser httpRequestParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestHandler = new RequestHandler();
        this.httpRequestParser = new HttpRequestParser();
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

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = httpRequestParser.parse(bufferedReader);
            final var response = requestHandler.handleRequest(request);
            response.writeTo(outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
