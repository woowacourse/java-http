package com.techcourse.controller;

import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class ApiRouter {

    private static final LoginController loginController = new LoginController();
    private static final RegisterController registerController = new RegisterController();

    private static final Map<MethodAndPath, Function<HttpRequest, HttpResponse>> routingTable = Map.of(
            new MethodAndPath("GET", "/login"), loginController::doGet,
            new MethodAndPath("POST", "/login"), loginController::doPost,
            new MethodAndPath("GET", "/register"), registerController::doGet,
            new MethodAndPath("POST", "/register"), registerController::doPost
    );

    public static HttpResponse route(String method, String path, HttpRequest request) {
        MethodAndPath methodAndPath = new MethodAndPath(method, path);
        if (!routingTable.containsKey(methodAndPath)) {
            throw new IllegalArgumentException("Handler not found for " + method + " " + path);
        }
        return routingTable.get(methodAndPath).apply(request);
    }

    record MethodAndPath(String method, String path) {
    }
}
