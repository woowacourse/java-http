package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;

//요청 경로에 맞는 컨트롤러를 찾아줌. if절 분기를 대신함
public class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private final Controller staticResourceController = new StaticResourceController();

    public Controller getController(String path) {
        return controllers.getOrDefault(path, staticResourceController);
    }
}
