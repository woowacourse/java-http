package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;

// URL 경로와 컨트롤러를 각각 매핑해둔 클래스
public class RequestMapping {

    private static final Map<String, Controller> controllerMap = new HashMap<>();
    private static final Controller DEFAULT_CONTROLLER = new StaticResourceController();

    static {
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public static Controller getController(String path) {
        return controllerMap.getOrDefault(path, DEFAULT_CONTROLLER);
    }
}
