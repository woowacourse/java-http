package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller defaultController = new StaticResourceController();

    public RequestMapping() {
        UserService userService = new UserService();
        Controller loginController = new LoginController(userService);
        Controller registerController = new RegisterController(userService);
        this.controllers = Map.of(
                "/login", loginController,
                "/login.html", loginController,
                "/register", registerController,
                "/register.html", registerController
        );
    }

    public void service(HttpRequest request, HttpResponse response) throws Exception {
        getController(request).service(request, response);
    }

    private Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), defaultController);
    }
}
