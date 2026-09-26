package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.net.Socket;
import org.apache.coyote.HttpHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpHandler handler;

    public Http11Processor(final Socket connection, final HttpHandler handler) {
        this.connection = connection;
        this.handler = handler;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
            final var outputStream = connection.getOutputStream()) {
            final var readLine = HttpRequestParser.readLine(inputStream);
            if (readLine == null) {
                return;
            }

            final var request = HttpRequestParser.parse(readLine, inputStream);
            final HttpResponse response = new HttpResponse();
            handler.handle(request, response);
            HttpResponseWriter.write(response, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
