package nextstep.jwp.http.request;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.StaticResourceController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapping(Map<String, Controller> controllers, StaticResourceController staticResourceController) {
        this.controllers = new ConcurrentHashMap<>(controllers);
        this.staticResourceController = staticResourceController;
    }

    public Controller getController(String uri) {
        return controllers.getOrDefault(uri, staticResourceController);
    }
}

