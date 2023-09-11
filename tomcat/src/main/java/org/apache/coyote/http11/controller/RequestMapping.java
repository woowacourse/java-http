package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    private RequestMapping() {
    }

    public static RequestMapping init() {
        registerController();
        return new RequestMapping();
    }

    private static void registerController() {
        controllers.put("/", new IndexPageController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String requestURI = request.getHttpRequestStartLine().getPath();
        return controllers.getOrDefault(requestURI, new StaticResourceController());
    }
}
