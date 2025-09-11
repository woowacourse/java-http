package org.apache.coyote.http11.processor;

import com.techcourse.HttpStaus;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.router.RequestMapping;
import org.apache.coyote.http11.util.HttpRequestParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = RequestMapping.getInstance();

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Optional<HttpRequest> request = HttpRequestParser.parse(inputStream);
            if (request.isEmpty()) {
                return;
            }
            HttpResponse response = new HttpResponse(outputStream);

            Optional<Controller> controller = REQUEST_MAPPING.getController(request.get());
            if (controller.isPresent()) {
                controller.get().service(request.get(), response);
            } else {
                handleStaticResource(request.get(), response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleStaticResource(HttpRequest request, HttpResponse response)
            throws IOException, URISyntaxException {
        if (request.getUrl().equals("/")) {
            ResponseHandler.sendDefaultResource(response);
            return;
        }
        try {
            ResponseHandler.sendStaticFile(response, request.getUrl(), HttpStaus.OK);
        } catch (IOException | URISyntaxException e) {
            ResponseHandler.sendStaticFile(response, "/404.html", HttpStaus.NOT_FOUND);
        }
    }
}
