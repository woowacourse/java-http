package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.apache.catalina.session.Session.JSESSIONID_COOKIE_NAME;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapper requestMapper = new RequestMapper();

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
        try (final var outputStream = connection.getOutputStream();
             final var inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest request = HttpRequestFactory.readFrom(bufferedReader);
            final HttpResponse response = new HttpResponse();

            makeSessionIfNotExist(request, response);

            final Controller controller = requestMapper.getController(request);
            controller.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void makeSessionIfNotExist(final HttpRequest request, final HttpResponse response) {
        final String sessionId = request.getCookie(JSESSIONID_COOKIE_NAME);
        if (SessionManager.findSession(sessionId) == null) {
            final UUID generatedSessionId = UUID.randomUUID();
            final Session session = new Session(generatedSessionId.toString());
            SessionManager.addSession(session);
            response.setCookie(JSESSIONID_COOKIE_NAME, session.getId());
        }
    }
}
