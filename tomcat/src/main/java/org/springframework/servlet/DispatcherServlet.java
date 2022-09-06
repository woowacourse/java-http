package org.springframework.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.coyote.Servlet;
import org.apache.http.BasicHttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.reflections.Reflections;
import org.richard.utils.CustomReflectionUtils;
import org.springframework.RequestMappingInfo;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;

public class DispatcherServlet implements Servlet {

    private static final String NOT_FOUND_PAGE = "static/404.html";

    private final Map<RequestMappingInfo, Function<HttpRequest, HttpResponse>> requestMapping;

    public DispatcherServlet() {
        this.requestMapping = new Reflections("nextstep")
                .getTypesAnnotatedWith(Controller.class)
                .stream()
                .filter(this::hasAnyMethodWithRequestMappingAnnotation)
                .flatMap(this::entry)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private boolean hasAnyMethodWithRequestMappingAnnotation(final Class<?> controller) {
        return Arrays.stream(controller.getMethods())
                .anyMatch(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class)));
    }

    private Stream<Entry<RequestMappingInfo, Function<HttpRequest, HttpResponse>>> entry(final Class<?> controller) {
        final var controllerInstance = CustomReflectionUtils.newInstance(controller);

        final var methods = Arrays.stream(controller.getMethods())
                .filter(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class)))
                .collect(Collectors.toList());

        return methods.stream()
                .map(method -> Map.entry(
                        RequestMappingInfo.from(method.getAnnotation(RequestMapping.class)),
                        CustomReflectionUtils.runnableByMethod(controllerInstance, method))
                );
    }

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return requestMapping.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getKey(), RequestMappingInfo.from(httpRequest)))
                .findAny()
                .orElseGet(() -> Map.entry(RequestMappingInfo.EMPTY, resourceHandler()))
                .getValue()
                .apply(httpRequest);
    }

    private Function<HttpRequest, HttpResponse> resourceHandler() {
        return new Function<HttpRequest, HttpResponse>() {
            @Override
            public HttpResponse apply(final HttpRequest httpRequest) {
                final var resourceResponse = getResponseBodyByURI(httpRequest);
                final var contentType = httpRequest.getContentType();

                return BasicHttpResponse.from(resourceResponse, contentType);
            }
        };
    }

    private String getResponseBodyByURI(final HttpRequest httpRequest) {
        final var requestURI = httpRequest.getRequestURI();
        final var resource = findResource(requestURI);
        final var path = new File(resource.getFile()).toPath();

        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL findResource(final String requestURI) {
        final var classLoader = getClass().getClassLoader();
        final var resource = classLoader.getResource(String.format("static%s", requestURI));

        if (Objects.nonNull(resource)) {
            return resource;
        }

        return classLoader.getResource(NOT_FOUND_PAGE);
    }
}
