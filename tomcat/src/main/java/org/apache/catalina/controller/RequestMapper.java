package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;

public class RequestMapper {

    private final Map<String, Controller> controllers;

    // TODO: map을 외부에서 등록할 수 있도록 개선 필요
    public RequestMapper() {
        this.controllers = Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        return new DefaultController();
    }
}