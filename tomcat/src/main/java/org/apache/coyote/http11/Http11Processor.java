package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.catalina.handler.Handler;
import org.apache.catalina.handler.NotFoundHandler;
import org.apache.catalina.handler.StaticResourceHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.router.HttpRequestRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<Handler> handlers = List.of(
            new StaticResourceHandler(),
            new NotFoundHandler()
    );

    private final Socket connection;

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
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final OutputStream outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = HttpRequest.from(reader);
            final HttpResponse response = new HttpResponse();

            final HttpRequestRouter router = new HttpRequestRouter(new RequestMapping());
            router.initialize();
            boolean routed = router.route(request, response);

            if (!routed) {
                handleWithHandlers(request, response);
            }

            sendResponse(response, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleWithHandlers(HttpRequest request, HttpResponse response) throws IOException {
        handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseGet(() -> new NotFoundHandler())
                .handle(request, response);
    }

    private void sendResponse(HttpResponse response, OutputStream outputStream) throws IOException {
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
