package nextstep.jwp.infrastructure.http;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.infrastructure.http.handler.ControllerHandler;
import nextstep.jwp.infrastructure.http.handler.FileHandler;
import nextstep.jwp.infrastructure.http.handler.Handler;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.RequestLine;

public class HandlerMapping {

    private final Map<String, Controller> controllers;
    private final FileHandler fileHandler;

    public HandlerMapping(final String controllerPackage, final FileHandler fileHandler) {
        this.controllers = findAllControllers(controllerPackage).stream()
            .collect(Collectors.toMap(Controller::uri, controller -> controller));
        this.fileHandler = fileHandler;
    }

    private static Set<Controller> findAllControllers(final String controllerPackage) {
        final ClassScanner classScanner = new ClassScanner(controllerPackage);
        return classScanner.scanAs(Controller.class);
    }

    public Handler getHandler(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        final String baseUri = requestLine.getUri().getBaseUri();

        return Optional.ofNullable(controllers.getOrDefault(baseUri, null))
            .map(controller -> (Handler) new ControllerHandler(controller))
            .orElse(fileHandler);
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }
}
