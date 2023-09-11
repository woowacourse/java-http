package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.FrontController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController;

    public Http11Processor(final Socket connection, FrontController frontController) {
        this.connection = connection;
        this.frontController = frontController;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            final HttpRequest request = HttpRequest.from(bufferedReader);
            final HttpResponse response = HttpResponse.create();
            final Controller controller = frontController.getController(request);
            controller.service(request, response);

            bufferedWriter.write(response.message());
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
