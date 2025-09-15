package org.apache.coyote.http11.processor;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.handler.StaticResourceHandler;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticResourceHandler staticResourceHandler;
    private final ControllerMapper controllerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.staticResourceHandler = new StaticResourceHandler();
        this.controllerMapper = new ControllerMapper();
        initializeControllers();
    }

    private void initializeControllers() {
        controllerMapper.addController("/login", new LoginController());
        controllerMapper.addController("/register", new RegisterController());
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

            final var httpRequest = HttpRequest.from(bufferedReader);
            final var httpResponse = new HttpResponse(httpRequest.getHttpVersion());

            final var session = SessionManager.resolveSession(httpRequest, httpResponse);

            httpRequest.setSession(session);

            final Controller controller = controllerMapper.getController(httpRequest.getPath());

            executeHandler(controller, httpRequest, httpResponse);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void executeHandler(final Controller controller, final HttpRequest httpRequest,
                                final HttpResponse httpResponse) throws Exception {
        if (controller != null) {
            controller.service(httpRequest, httpResponse);
            return;
        }
        staticResourceHandler.execute(httpRequest, httpResponse);
    }
}
