package org.apache.coyote.http11;

import org.apache.catalina.Manager;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionContext;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Manager sessionManager = SessionManager.getInstance();
    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestMapping());
    }

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
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

            HttpRequest request = HttpRequest.parse(inputStream);
            if (request == null) {
                return;
            }

            SessionContext sessionContext = new SessionContext(sessionManager, request.getCookie("JSESSIONID"));
            request.setSessionContext(sessionContext);

            HttpResponse response = new HttpResponse();
            requestMapping.getController(request).service(request, response);

            if (sessionContext.isChanged()) {
                response.setCookie("JSESSIONID", sessionContext.getSession().getId(), "/");
            }
            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
