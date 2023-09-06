package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http.controller.HttpController;
import org.apache.coyote.http.controller.HttpControllers;
import org.apache.coyote.http.controller.ViewRenderer;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestDecoder;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ViewRenderer VIEW_RENDERER = new ViewRenderer();

    private final Socket connection;
    private final HttpControllers httpControllers;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpControllers = HttpControllers.readControllers();
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
            HttpRequestDecoder requestParser = new HttpRequestDecoder();
            HttpRequest httpRequest = requestParser.decode(inputStream);
            Optional<String> sessionId = httpRequest.getSessionId();

            Session session = SessionManager.findSession(sessionId.orElse(null));

            HttpResponse httpResponse = new HttpResponse();
            HttpController controller = httpControllers.get(httpRequest.getPath());
            if (controller != null) {
                controller.service(httpRequest, httpResponse, session);
            }

            if (!httpResponse.isCompleted()) {
                VIEW_RENDERER.render(httpRequest, httpResponse);
            }

            SessionManager.manageSession(httpResponse, session);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
