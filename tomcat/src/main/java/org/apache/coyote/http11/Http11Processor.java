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

            HttpResponse response = new HttpResponse();
            HttpRequest request;
            try {
                request = HttpRequest.parse(inputStream);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpStatus.BAD_REQUEST, "올바르지 않은 HTTP 요청입니다.");
                response.writeTo(outputStream);
                return;
            }
            if (request == null) {
                return;
            }

            try {
                SessionContext sessionContext = new SessionContext(sessionManager, request.getCookie("JSESSIONID"));
                request.setSessionContext(sessionContext);

                requestMapping.getController(request).service(request, response);

                if (sessionContext.isChanged()) {
                    response.setCookie("JSESSIONID", sessionContext.getSession().getId(), "/");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                response = new HttpResponse();
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
            }
            response.writeTo(outputStream, !request.getMethod().equals("HEAD"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
