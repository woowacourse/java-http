package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;
    private final Controller controller;

    public Http11Processor(final Socket connection, final Controller controller) {
        this.connection = connection;
        httpRequestParser = new HttpRequestParser();
        this.controller = controller;
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
            final var request = httpRequestParser.accept(inputStream);
            final var response = HttpResponse.prepareFrom(request);

            controller.service(request, response);

            outputStream.write(response.buildResponse().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
