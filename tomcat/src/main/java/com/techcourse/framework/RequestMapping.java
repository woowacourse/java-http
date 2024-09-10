package com.techcourse.framework;

import com.techcourse.app.controller.HomeController;
import com.techcourse.app.controller.LoginController;
import com.techcourse.app.controller.RegisterController;
import com.techcourse.app.service.UserService;
import com.techcourse.framework.handler.Controller;
import com.techcourse.framework.handler.ResourceController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.protocol.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller resourceController;

    public RequestMapping() {
        this.controllers = new HashMap<>();
        this.resourceController = new ResourceController();

        UserService userService = new UserService();

        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController(userService));
        controllers.put("/register", new RegisterController(userService));
    }

    public RequestMapping(Map<String, Controller> controllers) {
        this.controllers = controllers;
        this.resourceController = new ResourceController();
    }

    public Controller getController(HttpRequest request) {
        Controller controller = controllers.get(request.getPath());
        if (controller == null) {
            return resourceController;
        }
        return controller;
    }
}

