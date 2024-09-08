package org.apache.catalina.controller;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {

    private final Map<HttpEndpoint, BiConsumer<HttpRequest, HttpResponse>> handlers;

    public AbstractController(Map<HttpEndpoint, BiConsumer<HttpRequest, HttpResponse>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpEndpoint httpEndpoint = HttpEndpoint.of(httpRequest);
        handlers.get(httpEndpoint).accept(httpRequest, httpResponse);
    }

    public List<HttpEndpoint> getEndpoints() {
        return handlers.keySet()
                .stream()
                .toList();
    }
}
