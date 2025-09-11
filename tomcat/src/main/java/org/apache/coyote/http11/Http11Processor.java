package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticFileController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.SessionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager manager;
    private final Map<String, Controller> controllerMap = new HashMap<>();
    private final Controller staticFileController = new StaticFileController();

    public Http11Processor(final Socket connection, final Manager manager) {
        this.connection = connection;
        this.manager = manager;
        controllerMap.put("/login", new LoginController(manager));
        controllerMap.put("/register", new RegisterController());
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
        ) {
            final var request = HttpRequest.from(inputStream);
            final var response = new HttpResponse(outputStream);

            final var session = SessionSupport.findSessionOrCreate(manager, request.getRequestCookies(), response);
            final var controller = controllerMap.getOrDefault(request.getPath(), staticFileController);
            controller.service(request, response, session);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try (final var outputStream = connection.getOutputStream()) {
                final var response = new HttpResponse(outputStream);
                response.sendServerError();
            } catch (IOException ioEx) {
                log.error("500 에러 전송 실패: {}", ioEx.getMessage(), ioEx);
            }
        }
    }
}
