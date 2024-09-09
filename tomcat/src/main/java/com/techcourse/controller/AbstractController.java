package com.techcourse.controller;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, Function<HttpRequest, HttpResponse>> methodHandlers;

    protected AbstractController() {
        this.methodHandlers = new EnumMap<>(HttpMethod.class);
        initMethodHandlers();
    }

    protected void registerHandler(final HttpMethod method, final Function<HttpRequest, HttpResponse> handler) {
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
    public HttpResponse service(final HttpRequest request) {
        return methodHandlers.getOrDefault(request.getMethod(), this::methodNotAllowed)
                .apply(request);
    }

    protected HttpResponse methodNotAllowed(final HttpRequest request) {
        return new HttpResponse(HttpStatusCode.METHOD_NOT_ALLOWED, new Headers(), "HTTP/1.1", new byte[]{});
    }

    protected HttpResponse doPost(final HttpRequest request) {
        return methodNotAllowed(request);
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return methodNotAllowed(request);
    }

    protected HttpResponse doPut(final HttpRequest request) {
        return methodNotAllowed(request);
    }

    protected HttpResponse doDelete(final HttpRequest request) {
        return methodNotAllowed(request);
    }

    protected HttpResponse doPatch(final HttpRequest request) {
        return methodNotAllowed(request);
    }
}
