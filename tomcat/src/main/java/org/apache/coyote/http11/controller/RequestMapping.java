package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    private RequestMapping() {
    }

    public static RequestMapping init() {
        registerController();
        return new RequestMapping();
    }

    private static void registerController() {
    }

    public Controller getController(HttpRequest request) {
        String requestURI = request.getHttpRequestStartLine().getPath();
        return controllers.get(requestURI);
    }
}
