package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> methodHandlers;

    protected AbstractController() {
        this.methodHandlers = new EnumMap<>(HttpMethod.class);
        initMethodHandlers();
    }

    protected void registerHandler(final HttpMethod method, final BiConsumer<HttpRequest, HttpResponse> handler) {
        methodHandlers.put(method, handler);
    }

    private void initMethodHandlers() {
        registerHandler(HttpMethod.GET, this::doGet);
        registerHandler(HttpMethod.POST, this::doPost);
        registerHandler(HttpMethod.PUT, this::doPut);
        registerHandler(HttpMethod.DELETE, this::doDelete);
        registerHandler(HttpMethod.PATCH, this::doPatch);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        methodHandlers.getOrDefault(request.getMethod(), this::methodNotAllowed)
                .accept(request, response);
    }

    protected void methodNotAllowed(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
        methodNotAllowed(request, response);
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
        methodNotAllowed(request, response);
    }

    protected void doPut(final HttpRequest request, final HttpResponse response) {
        methodNotAllowed(request, response);
    }

    protected void doDelete(final HttpRequest request, final HttpResponse response) {
        methodNotAllowed(request, response);
    }

    protected void doPatch(final HttpRequest request, final HttpResponse response) {
        methodNotAllowed(request, response);
    }
}
