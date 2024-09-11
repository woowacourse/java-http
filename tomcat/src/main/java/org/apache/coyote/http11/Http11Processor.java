package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.controller.FrontController;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController = FrontController.getInstance();

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
        try (
                final var inputStream = connection.getInputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = new HttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse();
            log.info("Requested endpoint: {}, Method: {}", request.getURI(), request.getHttpMethod());

            frontController.handle(request, response);

            outputStream.write(response.serialize().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
