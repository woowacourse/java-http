package com.techcourse.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.SessionManager;

public class RequestMapping {
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private final Map<String, Controller> controllerMap;
    private final Controller staticResourceController = new StaticResourceController();

    public RequestMapping(SessionManager sessionManager) {
        Map<String, Controller> controllerMap = new LinkedHashMap<>();
        controllerMap.put(ROOT_PATH, new RootController());
        controllerMap.put(LOGIN_PATH, new LoginController(sessionManager));
        controllerMap.put(REGISTER_PATH, new RegisterController());
        this.controllerMap = controllerMap;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getUri().getPath();
        return controllerMap.getOrDefault(path, staticResourceController);
    }
}
