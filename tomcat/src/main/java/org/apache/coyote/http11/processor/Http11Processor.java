package org.apache.coyote.http11.processor;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerMapper;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceHandler;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.util.HttpRequestUtil;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticResourceHandler resourceHandler;
    private final ControllerMapper controllerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceHandler = new StaticResourceHandler();
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var httpRequest = HttpRequestUtil.doParse(inputStream);
            final var httpResponse = new HttpResponse();

            final var session = SessionManager.resolveSession(httpRequest, httpResponse);

            httpRequest.setSession(session);

            final Controller controller = controllerMapper.getController(httpRequest.getPath());
            if (controller != null) {
                controller.service(httpRequest, httpResponse);
            } else {
                resourceHandler.execute(httpRequest, httpResponse);
            }

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
