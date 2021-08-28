package nextstep.jwp.infrastructure.http;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.infrastructure.http.controller.Controller;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;

public class ControllerMapping {

    private final Map<HttpRequestLine, Controller> controllers;

    public ControllerMapping(final List<Controller> controllers) {
        this.controllers = controllers.stream()
            .collect(Collectors.toMap(Controller::requestLine, controller -> controller));
    }

    public View handle(final HttpRequest request) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestLine requestLineWithoutQuery = new HttpRequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());
        if (!contains(request)) {
            return View.buildByResource(HttpStatusCode.NOT_FOUND, "/404.html");
        }

        return controllers.get(requestLineWithoutQuery).handle(request);
    }

    public boolean contains(final HttpRequest request) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestLine requestLineWithoutQuery = new HttpRequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());
        return controllers.containsKey(requestLineWithoutQuery);
    }
}
