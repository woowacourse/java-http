package com.techcourse.controller;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

public class FrontController implements RequestHandler {

    private final Map<String, Controller> controllers = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private final Controller staticResourceController = new StaticResourceController();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final var controller = handle(request);
        controller.service(request, response);
    }

    private Controller handle(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
