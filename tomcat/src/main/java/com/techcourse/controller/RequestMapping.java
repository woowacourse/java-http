package com.techcourse.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

public class RequestMapping implements RequestHandler {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController());

    private static final Controller DEFAULT_CONTROLLER = new StaticResourceController();

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        getController(request).service(request, response);
    }

    private Controller getController(final HttpRequest request) {
        return CONTROLLERS.getOrDefault(request.getPath(), DEFAULT_CONTROLLER);
    }
}
