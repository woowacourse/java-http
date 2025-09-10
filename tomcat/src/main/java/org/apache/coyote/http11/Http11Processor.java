package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerProvider;
import org.apache.coyote.http11.exception.InternalServerErrorException;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;
    private final ControllerProvider controllerProvider;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = SessionManager.INSTANCE;
        this.controllerProvider = ControllerProvider.INSTANCE;
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
            HttpRequest httpRequest = HttpRequest.from(inputStream, sessionManager);
            HttpResponse httpResponse = HttpResponse.createEmptyResponse();

            resolveController(httpRequest, httpResponse);
            handleSessionCreation(httpResponse);

            String response = httpResponse.getResponseFormat();
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void resolveController(final HttpRequest request, final HttpResponse response) {
        final String path = request.getPath();
        try {
            final Controller controller = controllerProvider.findByPath(path);
            controller.service(request, response);
        } catch (InternalServerErrorException e) {
            log.warn("존재하지 않는 path 입니다: %s".formatted(path));
            response.setInternalServerError();
        }
    }

    private void handleSessionCreation(final HttpResponse httpResponse) {
        Object userAttribute = httpResponse.getAttribute("session_user");
        if (userAttribute instanceof User) {
            final String sessionId = UUID.randomUUID().toString();
            final HttpSession session = new Session(sessionId);

            session.setAttribute("user", userAttribute);
            sessionManager.add(session);

            httpResponse.setCookie("JSESSIONID", sessionId);
        }
    }
}
