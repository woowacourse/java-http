package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HelloController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.FileController;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping(List.of(
            new HelloController(),
            new LoginController(),
            new FileController(),
            new RegisterController()
    ));

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();
            requestMapping.handle(httpRequest, httpResponse);

            outputStream.write(httpResponse.toLine().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
