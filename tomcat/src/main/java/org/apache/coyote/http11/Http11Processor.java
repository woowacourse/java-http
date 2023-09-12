package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.FileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final Controller DEFAULT_CONTROLLER = new FileController();
    private static final LoginController LOGIN_CONTROLLER = new LoginController();
    private static final RegisterController REGISTER_CONTROLLER = new RegisterController();
    private static final Map<String, Controller> PREDEFINED_CONTROLLERS = Map.of(
            "/", (request, response) -> response.setBody("Hello world!"),
            "/login", LOGIN_CONTROLLER,
            "/login.html", LOGIN_CONTROLLER,
            "/register", REGISTER_CONTROLLER,
            "/register.html", REGISTER_CONTROLLER
    );
    private static final String WHITE_SPACE = " ";

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();
            handle(httpRequest, httpResponse);

            outputStream.write(httpResponse.toLine().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (PREDEFINED_CONTROLLERS.containsKey(httpRequest.getTarget().getPath())) {
            PREDEFINED_CONTROLLERS.get(httpRequest.getTarget().getPath())
                    .handle(httpRequest, httpResponse);
            return;
        }
        DEFAULT_CONTROLLER.handle(httpRequest, httpResponse);
    }
}
