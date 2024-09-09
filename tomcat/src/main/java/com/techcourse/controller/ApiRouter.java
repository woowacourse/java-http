package com.techcourse.controller;

import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;

public class ApiRouter {

    private static final LoginController loginController = new LoginController();
    private static final RegisterController registerController = new RegisterController();

    private static final Map<MethodAndPath, Function<HttpRequest, HttpResponse>> routingTable = Map.of(
            new MethodAndPath(HttpMethod.GET, "/login"), loginController::doGet,
            new MethodAndPath(HttpMethod.POST, "/login"), loginController::doPost,
            new MethodAndPath(HttpMethod.GET, "/register"), registerController::doGet,
            new MethodAndPath(HttpMethod.POST, "/register"), registerController::doPost
    );

    public static HttpResponse route(HttpMethod method, String path, HttpRequest request) {
        MethodAndPath methodAndPath = new MethodAndPath(method, path);
        if (!routingTable.containsKey(methodAndPath)) {
            throw new IllegalArgumentException("Handler not found for " + method + " " + path);
        }
        return routingTable.get(methodAndPath).apply(request);
    }

    record MethodAndPath(HttpMethod method, String path) {

    }
}
