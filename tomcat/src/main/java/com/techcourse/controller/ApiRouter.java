package com.techcourse.controller;

import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class ApiRouter {

    private static final LoginController loginController = new LoginController();

    private static final Map<MethodAndPath, Function<HttpRequest, HttpResponse>> routingTable = Map.of(
            new MethodAndPath("GET", "/login"), loginController::login
    );

    public static HttpResponse route(String method, String path, HttpRequest request) {
        final var handler = routingTable.get(new MethodAndPath(method, path));
        if (handler == null) {
            throw new IllegalArgumentException("Handler not found for " + method + " " + path);
        }
        return handler.apply(request);
    }

    record MethodAndPath(String method, String path) {
    }
}
