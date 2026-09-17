package org.apache.coyote.http11;

import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);


    private final Socket connection;
    private final Adapter adapter;
    private final HttpResponseWriter responseWriter;

    public Http11Processor(final Socket connection, final Adapter adapter) {
        this.connection = connection;
        this.adapter = adapter;
        this.responseWriter = new HttpResponseWriter();
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

            HttpServletRequest request = new Http11RequestParser(inputStream).parse();

            HttpServletResponse response = adapter.service(request);

            responseWriter.write(response, outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
