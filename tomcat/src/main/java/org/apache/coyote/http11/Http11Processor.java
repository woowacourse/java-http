package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.catalina.container.http.request.HttpRequest;
import org.apache.catalina.container.http.response.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final FrontController frontController = new FrontController();

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

            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse(request.getVersion());

            frontController.service(request, response);

            response.writeMessage(outputStream);

        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }
}
