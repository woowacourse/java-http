package org.apache.coyote.http11.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.http11.request.annotation.RequestMapping;
import org.apache.coyote.http11.request.annotation.RequestParam;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.AppConfig;
import nextstep.jwp.controller.Controller;

public class RequestHandler {

    private static final Controller controller = AppConfig.getInstance().controller;

    public String handle(final HttpRequest request) {
        Optional<Method> method = findRequestMappedMethod(request.getMethod(), request.getPath());

        if (method.isPresent()) {
            return service(method.get(), request);
        }
        return resource(request.getPath());
    }

    private Optional<Method> findRequestMappedMethod(final RequestMethod requestMethod, final String path) {
        return Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .filter(method -> containsPath(method, requestMethod, path))
                .findAny();
    }

    private boolean containsPath(final Method method, final RequestMethod requestMethod, final String path) {
        final RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
        return containsMethod(annotation, requestMethod) && containsPath(annotation, path);
    }

    private boolean containsMethod(final RequestMapping annotation, final RequestMethod requestMethod) {
        final List<RequestMethod> methods = List.of(annotation.method());
        return methods.contains(requestMethod);
    }

    private boolean containsPath(final RequestMapping annotation, final String path) {
        final List<String> paths = List.of(annotation.value());
        return paths.contains(path);
    }

    private String service(final Method method, final HttpRequest request) {
        try {
            final List<Parameter> parameters = List.of(method.getParameters());
            if (parameters.isEmpty()) {
                HttpResponse response = (HttpResponse) method.invoke(controller);
                return response.asFormat();
            }

            final Params params = getParamsByRequestMethod(request);
            final String[] args = parameters.stream()
                    .map(parameter -> parameter.getDeclaredAnnotation(RequestParam.class))
                    .map(parameter -> params.find(parameter.value()))
                    .toArray(String[]::new);

            final HttpResponse response = (HttpResponse) method.invoke(controller, args);
            return response.asFormat();

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    private Params getParamsByRequestMethod(final HttpRequest request) {
        final RequestMethod requestMethod = request.getMethod();

        if (requestMethod.equals(RequestMethod.GET)) {
            return request.getParamsFromUri();
        }
        if (requestMethod.equals(RequestMethod.POST)) {
            return request.getParamsFromBody();
        }
        throw new IllegalArgumentException();
    }

    private String resource(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath))
                .asFormat();
    }
}
