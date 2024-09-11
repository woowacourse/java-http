package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;

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
            final var request = new HttpRequest(inputStream);
            log.info("request: \n{}", request.asString());
            final var controller = new FrontController();
            final var response = controller.handle(request);
            outputStream.write(response.getBytes());
            log.info("response: \n{}", response);
            log.info("Http11Processor.process(): Written!!");
            outputStream.flush();
            log.info("Http11Processor.process(): Flushed!!");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
