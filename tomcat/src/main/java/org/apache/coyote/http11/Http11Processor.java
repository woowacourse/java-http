package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.session.SessionManager;
import org.apache.container.ServletContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletContainer servletContainer;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.servletContainer = new ServletContainer();
        this.sessionManager = SessionManager.connect();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {
            Http11Request http11Request = Http11Request.of(bufferedReader);
            Http11Response http11Response = new Http11Response();
            if (http11Request.getSessionId() == null) {
                String sessionId = sessionManager.createSession();
                http11Request.setSessionId(sessionId);
                http11Response.setSession(sessionId);
            }

            log.info(http11Request.getUri());
            servletContainer.process(http11Request, http11Response);

            final var response = http11Response.toResponseFormat();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
