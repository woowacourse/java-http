package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.handler.FrontController;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.apache.coyote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final FrontController FRONT_CONTROLLER = new FrontController();
    private static final HttpRequestParser HTTP_REQUEST_PARSER = new HttpRequestParser();

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
            HttpRequest httpRequest = HTTP_REQUEST_PARSER.convertToHttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = FRONT_CONTROLLER.handle(httpRequest);
            controller.process(httpRequest, httpResponse);

            outputStream.write(httpResponse.joinResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
