package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static final ViewRenderer viewRenderer = new ViewRenderer();

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequestDecoder requestDecoder = new HttpRequestDecoder();
            HttpRequest httpRequest = requestDecoder.decode(bufferedReader);

            Optional<String> sessionId = httpRequest.getSessionId();
            Session session = SessionManager.findSession(sessionId.orElse(null));

            HttpResponse httpResponse = new HttpResponse();
            Optional<HttpController> controller = httpControllers.get(httpRequest.getPath());
            controller.ifPresent(
                httpController -> httpController.service(httpRequest, httpResponse, session)
            );

            if (!httpResponse.isCompleted()) {
                viewRenderer.render(httpRequest, httpResponse);
            }

            SessionManager.manageSession(httpResponse, session);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
