package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final SessionManager sessionManager;

    public Http11Processor(Socket connection, RequestMapping requestMapping) {
        this(connection, requestMapping, new SessionManager());
    }

    public Http11Processor(Socket connection, RequestMapping requestMapping, SessionManager sessionManager) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {
            HttpResponse response = new HttpResponse(outputStream);
            try {
                HttpRequest request = new HttpRequest(inputStream, sessionManager);
                Controller controller = requestMapping.getController(request);
                controller.service(request, response);
                addSessionCookie(request, response);
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage());
                response.reset(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                response.reset(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            response.send();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookie(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCreatedSessionId();
        if (sessionId != null) {
            response.addCookie(HttpCookie.JSESSION_ID, sessionId);
        }
    }
}
