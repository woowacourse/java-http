package org.apache.coyote.http11.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (method.isEmpty()) {
            return resource(request.getPath());
        }

        try {
            Method m = method.get();

            List<Parameter> parameters = List.of(m.getParameters());
            if (parameters.isEmpty()) {
                HttpResponse response = (HttpResponse) m.invoke(controller);
                return response.asFormat();
            }


            if (request.getMethod().equals(RequestMethod.GET)) {
                Params params = request.getParamsFromUri();
                String[] args = parameters.stream()
                        .map(parameter -> params.find(parameter.getDeclaredAnnotation(RequestParam.class).value()))
                        .toArray(String[]::new);

                HttpResponse response = (HttpResponse) m.invoke(controller, args);
                return response.asFormat();
            }

            if (request.getMethod().equals(RequestMethod.POST)) {
                Params params = request.getParamsFromBody();
                String[] args = parameters.stream()
                        .map(parameter -> params.find(parameter.getDeclaredAnnotation(RequestParam.class).value()))
                        .toArray(String[]::new);

                HttpResponse response = (HttpResponse) m.invoke(controller, args);
                return response.asFormat();
            }

            throw new RuntimeException();
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private Optional<Method> findRequestMappedMethod(final RequestMethod requestMethod, final String path) {
        final List<Method> methods = Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toUnmodifiableList());

        return methods.stream()
                .filter(method -> containsPath(method, requestMethod, path))
                .findAny();
    }

    private boolean containsPath(final Method method, final RequestMethod requestMethod, final String path) {
        final RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
        final List<RequestMethod> methods = List.of(annotation.method());
        final List<String> paths = List.of(annotation.value());
        return methods.contains(requestMethod) && paths.contains(path);
    }

    private String resource(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath))
                .asFormat();
    }
}
