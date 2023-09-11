package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    private RequestMapping() {
        registerController();
    }

    public static RequestMapping init() {
        return new RequestMapping();
    }

    private void registerController() {
        controllers.put("/", new IndexPageController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String requestURI = request.getHttpRequestStartLine().getPath();
        return controllers.getOrDefault(requestURI, new StaticResourceController());
    }
}
