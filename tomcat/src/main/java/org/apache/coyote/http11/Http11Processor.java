package org.apache.coyote.http11;

import com.techcourse.db.SessionManager;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.Session;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceHandler resourceHandler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceHandler = new ResourceHandler();
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

            final var httpRequest = HttpRequestParser.parse(inputStream);
            final var httpResponse = new HttpResponse();

            final Cookie cookie = httpRequest.getCookies();
            final String jsessionid = cookie.getCookie("JSESSIONID");

            Session session;
            if (jsessionid != null) {
                session = SessionManager.findSession(jsessionid).orElse(null);
            } else {
                session = null;
            }

            if (session == null) {
                session = SessionManager.createSession();
                httpResponse.addCookie(Cookie.ofJSessionId(session.getId()));
            }
            httpRequest.setSession(session);

            resourceHandler.execute(httpRequest, httpResponse);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
