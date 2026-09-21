package org.apache.coyote.http11;

import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
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
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequest.parse(inputStream);
            HttpResponse response = new HttpResponse();

            request.createJSessionIdIfAbsent().ifPresent(sessionId -> response.setCookie(HttpCookie.JSESSION_ID, sessionId));

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            response.writeTo(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("요청 처리 중 오류가 발생했습니다.", e);
        }
    }
}
