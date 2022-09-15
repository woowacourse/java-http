package org.springframework.servlet;

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
import org.apache.http.info.ContentType;
import org.apache.http.info.HttpVersion;
import org.apache.http.info.StatusCode;
import org.reflections.Reflections;
import org.richard.utils.CustomReflectionUtils;
import org.richard.utils.ResourceUtils;
import org.richard.utils.YamlUtils;
import org.springframework.annotation.Controller;
import org.springframework.annotation.RequestMapping;
import org.springframework.config.ApplicationConfig;

public class DispatcherServlet implements Servlet {

    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String DEFAULT_SPRING_CONFIG_FILE_NAME = "application.yml";

    private final Map<RequestMappingInfo, Function<HttpRequest, HttpResponse>> requestMapping;

    public DispatcherServlet() {
        final var config
                = YamlUtils.readPropertyAsObject(DEFAULT_SPRING_CONFIG_FILE_NAME, ApplicationConfig.class);
        final var basePackage = config.getBasePackage();

        this.requestMapping = new Reflections(basePackage)
                .getTypesAnnotatedWith(Controller.class)
                .stream()
                .filter(this::hasAnyMethodWithRequestMappingAnnotation)
                .flatMap(this::createRequestMapping)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private boolean hasAnyMethodWithRequestMappingAnnotation(final Class<?> controller) {
        return Arrays.stream(controller.getMethods())
                .anyMatch(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class)));
    }

    private Stream<Entry<RequestMappingInfo, Function<HttpRequest, HttpResponse>>> createRequestMapping(
            final Class<?> controller) {
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
        return httpRequest -> {
            final var accept = httpRequest.getHeader("Accept");
            var contentType = ContentType.TEXT_HTML.getName();
            if (Objects.nonNull(accept)) {
                contentType = accept.split(",")[0];
            }
            final var resourceAsBody = ResourceUtils.createResourceAsString(
                    String.format("static%s", httpRequest.getRequestURIWithoutQueryParams()));

            return BasicHttpResponse.builder()
                    .httpVersion(HttpVersion.HTTP_1_1)
                    .statusCode(StatusCode.OK_200)
                    .contentType(contentType)
                    .body(resourceAsBody)
                    .build();
        };
    }
}
