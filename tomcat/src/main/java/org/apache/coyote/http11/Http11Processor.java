package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.support.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping,
                           final SessionManager sessionManager) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var reader = new BufferedReader(streamReader);
             final var outputStream = connection.getOutputStream()) {

            final var request = HttpRequest.of(reader);
            final var response = new HttpResponse();
            final var controller = requestMapping.getController(request);
            sessionManager.updateSessionAndCookie(request, response);
            controller.service(request, response);

            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        } catch (IOException | HttpException e) {
            log.error(e.getMessage(), e);
        }
    }
}
