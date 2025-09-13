package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.request.HttpRequestParser;
import org.apache.coyote.util.response.HttpResponse;
import org.apache.coyote.util.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Adapter adapter;

    public Http11Processor(final Socket connection, final Adapter adapter) {
        this.connection = connection;
        this.adapter = adapter;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        HttpResponse response = new HttpResponse();
        try (final var inputStream = connection.getInputStream();
             final var outputStream = new BufferedOutputStream(connection.getOutputStream())) {

            HttpRequest request = HttpRequestParser.parse(inputStream);
            adapter.service(request, response);
            response.send(outputStream);
        } catch (Exception e) {
            log.error("process error: {}", e.getMessage(), e);
            adapter.handleError(response, HttpStatus.INTERNAL_SERVER_ERROR);
            try {
                response.send(connection.getOutputStream());
            } catch (IOException ex) {
                log.error("process send error: {}", ex.getMessage(), ex);
            }
        }
    }
}
