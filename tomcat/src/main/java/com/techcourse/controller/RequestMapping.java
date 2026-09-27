package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.Container;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping implements Container {

    private final Map<String, Controller> controllers = Map.of(
        "/", new HomeController(),
        "/login", new LoginController(),
        "/register", new RegisterController());

    private final Controller defaultController = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        controllers.getOrDefault(request.getPath(), defaultController)
            .service(request, response);
    }
}
