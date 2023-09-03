package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestDecoder;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.controller.HttpController;
import org.apache.coyote.http.controller.ViewRenderer;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ViewRenderer VIEW_RENDERER = new ViewRenderer();

    private final Socket connection;
    private Map<String, HttpController> controllers;

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
        readControllers();

        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequestDecoder requestParser = new HttpRequestDecoder();
            HttpRequest httpRequest = requestParser.decode(inputStream);

            HttpResponse httpResponse = new HttpResponse();
            HttpController controller = controllers.get(httpRequest.getPath());
            if (controller != null) {
                controller.service(httpRequest, httpResponse);
            }

            if (!httpResponse.isCompleted()) {
                VIEW_RENDERER.render(httpRequest, httpResponse);
            }

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readControllers() {
        this.controllers = new Reflections("nextstep.jwp.ui")
                .getSubTypesOf(HttpController.class)
                .stream()
                .collect(toMap(this::parsePath, this::getInstance));
    }

    private String parsePath(Class<? extends HttpController> clazz) {
        return "/" + clazz.getSimpleName().toLowerCase().replace("controller", "");
    }

    private HttpController getInstance(Class<? extends HttpController> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
