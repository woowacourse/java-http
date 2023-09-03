package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.request.RequestLine;
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
        this.handlerMapper = new HandlerMapper();
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
            RequestLine requestLine = new RequestLine(inputReader.readLine());
            String response = getResponse(requestLine);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(RequestLine requestLine) {
        if (resourceProvider.haveResource(requestLine.getPath())) {
            return staticResourceResponse(requestLine.getPath());
        }
        if (handlerMapper.haveAvailableHandler(requestLine)) {
            return controllerResponse(requestLine);
        }
        return staticResourceResponse("/404.html");
    }

    private String staticResourceResponse(String resourcePath) {
        String responseBody = resourceProvider.resourceBodyOf(resourcePath);
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            resourceProvider.contentTypeOf(resourcePath),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private String controllerResponse(RequestLine requestLine) {
        Controller handler = handlerMapper.getHandler(requestLine);
        String controllerResponse = handler.handle(requestLine);
        return staticResourceResponse(controllerResponse);
    }
}
