package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import org.apache.catalina.controller.Controller;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequest.from(inputStream);
            if (request == null) {
                return;
            }
            HttpResponse response = new HttpResponse();
            String sessionId = request.getCookie("JSESSIONID");

            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
                response.setHeader("Set-Cookie", "JSESSIONID=" + sessionId);
            }

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);

            if (session == null) {
                session = new Session(sessionId);
                sessionManager.add(session);
            }

            request.setSession(session);
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            response.writeTo(outputStream);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
