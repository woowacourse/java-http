package org.apache.coyote.http11.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.http11.request.mapping.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.service.Service;

public class RequestHandler {

    public String handle(final RequestHeader request) {
        final Uri uri = Uri.parse(request.getUri());

        Optional<Method> method = findRequestMappedMethodByPath(uri.getPath());

        if (method.isEmpty()) {
            return resource(uri.getPath());
        }

        try {
            Controller controller = new Controller(new Service());

            Method m = method.get();

            List<Parameter> parameters = List.of(m.getParameters());
            if (parameters.isEmpty()) {
                HttpResponse response = (HttpResponse) m.invoke(controller);
                return response.asFormat();
            }

            HttpResponse response = (HttpResponse) m.invoke(controller, uri.getParams());
            return response.asFormat();

        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private Optional<Method> findRequestMappedMethodByPath(final String path) {
        final List<Method> methods = Arrays.stream(Controller.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toUnmodifiableList());

        return methods.stream()
                .filter(method -> containsPath(method, path))
                .findAny();
    }

    private boolean containsPath(final Method method, final String path) {
        final RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
        final List<String> paths = List.of(annotation.value());
        return paths.contains(path);
    }

    private String resource(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath))
                .asFormat();
    }
}
