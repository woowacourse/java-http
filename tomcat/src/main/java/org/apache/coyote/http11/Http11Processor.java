package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);


    private final Socket connection;
    private final RequestMapping requestMapping;
    private final Controller staticResourceController;
    private final HttpSessionService sessionService;

    public Http11Processor(
            final Socket connection,
            final RequestMapping requestMapping,
            final Controller staticResourceController,
            final HttpSessionService sessionService
    ) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.staticResourceController = staticResourceController;
        this.sessionService = sessionService;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedInputStream inputStream =
                     new BufferedInputStream(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final Optional<HttpRequest> optionalRequest = HttpRequest.from(inputStream);

            if (optionalRequest.isEmpty()) {
                return;
            }
            final HttpRequest request = optionalRequest.get();
            final HttpResponse response = new HttpResponse();

            sessionService.ensureSessionIdCookie(request, response);
            serviceController(request, response);
            if (!response.hasStatus()) {// fallback구조
                staticResourceController.service(request, response);
            }
            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serviceController(
            final HttpRequest request, final HttpResponse response) throws Exception {
        final Optional<Controller> controller = requestMapping.getController(request);

        if (controller.isEmpty()) {
            return;
        }
        controller.get().service(request, response);
    }
}

