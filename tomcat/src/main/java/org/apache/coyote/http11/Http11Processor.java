package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (connection;
             final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest request = HttpRequest.from(reader, sessionManager);
            HttpResponse response = new HttpResponse();

            Controller controller = RequestMapping.getController(request.getUri());
            controller.service(request, response);

            ensureSessionCookie(request, response);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void ensureSessionCookie(HttpRequest request, HttpResponse response) {
        if (response.hasCookie("JSESSIONID")) {
            return;
        }
        String sessionId = request.getHeader().getCookieValue("JSESSIONID");
        if (sessionId == null) {
            Session session = request.getSession(true);
            response.addCookie(Cookie.ofJSessionId(session.getId()));
        }
    }
}
