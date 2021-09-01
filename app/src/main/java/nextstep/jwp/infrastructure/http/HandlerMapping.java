package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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

    private static final FileResolver FILE_RESOLVER = new FileResolver("static");
    private static final FileHandler FILE_HANDLER = new FileHandler(FILE_RESOLVER);

    private final Map<String, Controller> controllers;

    public HandlerMapping(final String controllerPackage) {
        this.controllers = findAllControllers(controllerPackage).stream()
            .collect(Collectors.toMap(Controller::uri, controller -> controller));
    }

    private static Set<Controller> findAllControllers(final String controllerPackage) {
        final ClassScanner<Controller> classScanner = new ClassScanner<>(controllerPackage);
        return classScanner.scanAs(Controller.class);
    }

    public Handler getHandler(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        final String baseUri = requestLine.getUri().getBaseUri();

        return Optional.ofNullable(controllers.getOrDefault(baseUri, null))
            .map(controller -> (Handler) new ControllerHandler(controller))
            .orElse(FILE_HANDLER);
    }
}
