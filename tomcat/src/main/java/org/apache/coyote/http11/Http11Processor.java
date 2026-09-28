package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest request = new HttpRequest(reader);
            final HttpResponse response = new HttpResponse();

            final Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            write(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
