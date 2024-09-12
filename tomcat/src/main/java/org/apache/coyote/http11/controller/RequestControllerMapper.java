package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestControllerMapper {

    private static final Map<String, Controller> controllerMap = new ConcurrentHashMap<>();

    static {
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public static Controller getController(String url) {
        return controllerMap.get(url);
    }
}
