package org.apache.coyote.http11;

import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.Controller;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final RequestMapping requestMapping;
    private final Socket connection;

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = new HttpRequest(inputStream);
            Controller controller = requestMapping.getController(httpRequest);
            HttpResponse httpResponse = controller.service(httpRequest);
            addSessionCookie(httpRequest, httpResponse);
            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookie(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getCookies().hasSessionId()) {
            return;
        }

        httpResponse.setCookie("JSESSIONID", httpRequest.getOrCreateSession().getId());
    }
}
