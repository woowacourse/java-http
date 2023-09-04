package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handle.DispatcherServlet;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.support.HttpRequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            final HttpRequest request = HttpRequestExtractor.extract(inputStream);
            final HttpResponse httpResponses = DispatcherServlet.from(request);

            outputStream.write(httpResponses.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException |RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }
}