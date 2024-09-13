package org.apache.coyote.request.mapper;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.request.HttpRequest;

public class RequestMapping {

    private final List<Controller> controllers;

    public RequestMapping() {
        this.controllers = initControllers();
    }

    public Optional<Controller> findController(HttpRequest httpRequest) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(httpRequest))
                .findFirst();
    }

    private List<Controller> initControllers() {
        return List.of(
                new DefaultController(),
                new LoginController(),
                new RegisterController(),
                new StaticResourceController()
        );
    }
}
