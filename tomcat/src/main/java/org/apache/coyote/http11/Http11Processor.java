package org.apache.coyote.http11;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.StaticResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final Controller staticResourceController;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping(SessionManager.getInstance());
        this.staticResourceController = new StaticResourceController();
    }


    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedInputStream inputStream =
                     new BufferedInputStream(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final Optional<HttpRequest> optionalRequest = HttpRequest.from(inputStream);

            if (optionalRequest.isEmpty()) {
                return;
            }
            final HttpRequest request = optionalRequest.get();
            final HttpResponse response = new HttpResponse();

            addSessionIdCookieIfAbsent(request, response);
            serviceController(request, response);
            if (!response.hasStatus()) {// fallback구조
                staticResourceController.service(request, response);
            }
            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionIdCookieIfAbsent(
            final HttpRequest request, final HttpResponse response) {
        final Optional<String> existingSessionId = request.getCookie(JSESSIONID);

        if (existingSessionId.isPresent() && !existingSessionId.get().isBlank()) {
            return;
        }

        final String newSessionId = UUID.randomUUID().toString();

        response.addHeader(SET_COOKIE, JSESSIONID + "=" + newSessionId);
    }

    private void serviceController(
            final HttpRequest request, final HttpResponse response) throws Exception {
        final Optional<Controller> controller = requestMapping.getController(request);

        if (controller.isEmpty()) {
            return;
        }
        controller.get().service(request, response);
    }
}

