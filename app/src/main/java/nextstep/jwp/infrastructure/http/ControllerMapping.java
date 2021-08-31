package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import nextstep.jwp.infrastructure.http.view.View;

public class ControllerMapping {

    private final Map<RequestLine, Controller> controllers;

    public ControllerMapping(final String controllerPackage) {
        this.controllers = findAllControllers(controllerPackage).stream()
            .collect(Collectors.toMap(Controller::requestLine, controller -> controller));
    }

    public Optional<View> handle(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        final RequestLine requestLineWithoutQuery = new RequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());

        return Optional.ofNullable(controllers.getOrDefault(requestLineWithoutQuery, null))
            .map(controller -> controller.handle(request));
    }

    private Set<Controller> findAllControllers(final String controllerPackage) {
        final Set<Class<?>> classes = findAllClassesUsingClassLoader(controllerPackage);
        return classes.stream()
            .filter(this::hasConstructor)
            .filter(this::hasNoArgumentConstructor)
            .map(this::findNoArgumentConstructor)
            .map(constructor -> {
                try {
                    return (Controller) constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException(String.format("Cannot invoke constructor. (%s)", constructor.getName()));
                }
            })
            .collect(Collectors.toSet());
    }

    private boolean hasConstructor(final Class<?> clazz) {
        return clazz.getDeclaredConstructors().length != 0;
    }

    private boolean hasNoArgumentConstructor(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
            .anyMatch(constructor -> constructor.getParameterTypes().length == 0);
    }

    private Constructor<?> findNoArgumentConstructor(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterTypes().length == 0)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find no-argument constructor."));
    }

    private Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        final InputStream stream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));
        return reader.lines()
            .filter(line -> line.endsWith(".class"))
            .map(line -> getClass(line, packageName))
            .collect(Collectors.toSet());
    }

    private Class<?> getClass(String className, String packageName) {
        final String classPath = packageName + "."
            + className.substring(0, className.lastIndexOf('.'));
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Cannot find class. (%s)", classPath));
        }
    }
}
