package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final Manager sessionManager,
                           final RequestMapping requestMapping) {
        this.connection = connection;
        this.sessionManager = sessionManager;
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
            final HttpRequest request;
            try {
                request = HttpRequest.readFrom(inputStream, sessionManager);
            } catch (IllegalArgumentException e) {
                return;
            }
            if (request == null) {
                return;
            }

            log.info("method: {}, path: {}, version: {}",
                    request.method(), request.path(), request.httpVersion());

            final HttpResponse response = new HttpResponse();
            requestMapping.getController(request).service(request, response);
            if (request.sessionCookie() != null) {
                response.header("Set-Cookie", request.sessionCookie());
            }
            response.writeTo(outputStream);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 인코딩입니다.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
