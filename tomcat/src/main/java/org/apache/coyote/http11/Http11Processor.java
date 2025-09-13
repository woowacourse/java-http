package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerProvider;
import org.apache.coyote.http11.exception.NotFoundException;
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
        } catch (NotFoundException e) {
            log.warn("존재하지 않는 path 입니다: %s".formatted(path));
            response.setNotFound();
        } catch (Exception e) {
            log.error("의도치 않은 에러가 발생했습니다", e);
            response.setInternalServerError();
        }
    }

    private void handleSessionCreation(final HttpResponse httpResponse) {
        final Set<String> sessionAttributeKeys = httpResponse.getAllAttributeKeys();
        if (sessionAttributeKeys.isEmpty()) {
            return;
        }

        final String sessionId = UUID.randomUUID().toString();
        final HttpSession session = new Session(sessionId);
        sessionAttributeKeys.forEach(key -> session.setAttribute(key, httpResponse.getAttribute(key)));
        sessionManager.add(session);

        httpResponse.setCookie("JSESSIONID", sessionId);
    }
}
