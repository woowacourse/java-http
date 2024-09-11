package com.techcourse.controller;

import org.apache.catalina.util.StaticResourceManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final List<Class<? extends Controller>> CONTROLLER_CLASSES = List.of(
            GreetingController.class,
            LoginController.class,
            RegisterController.class
    );

    private final StaticResourceController staticResourceController;
    private final Map<String, Controller> controllers;

    private static class RequestMappingHolder {
        private static final RequestMapping INSTANCE = new RequestMapping();
    }

    public static RequestMapping getInstance() {
        return RequestMappingHolder.INSTANCE;
    }

    private RequestMapping() {
        this.staticResourceController = new StaticResourceController();
        this.controllers = initControllers();
    }

    private Map<String, Controller> initControllers() {
        return CONTROLLER_CLASSES.stream()
                .map(clazz -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Failed to instantiate controller: " + clazz, e);
                    }
                })
                .collect(HashMap::new, (map, controller) -> map.put(controller.getPath(), controller), HashMap::putAll);
    }

    public Controller getController(HttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getPath();

        if (StaticResourceManager.isExist(path)) {
            return staticResourceController;
        }

        if (!controllers.containsKey(path)) {
            throw new IllegalArgumentException("Handler not found for " + method + " " + path);
        }
        return controllers.get(path);
    }
}
