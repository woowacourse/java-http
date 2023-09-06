package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProvider resourceProvider;
    private final HandlerMapper handlerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceProvider = new ResourceProvider();
        this.handlerMapper = new HandlerMapper(this.resourceProvider);
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.makeRequest(inputReader);
            String response = getResponse(httpRequest);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest) {
        if (resourceProvider.haveResource(httpRequest.getRequestLine().getPath())) {
            return resourceProvider.staticResourceResponse(httpRequest.getRequestLine().getPath());
        }
        if (handlerMapper.haveAvailableHandler(httpRequest)) {
            return handlerMapper.controllerResponse(httpRequest);
        }
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 ",
            "",
            "Hello world!");
    }
}
