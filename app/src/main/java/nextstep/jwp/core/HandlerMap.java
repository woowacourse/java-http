package nextstep.jwp.core;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.web.HttpRequest;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMap {
    private static final Map<URI, Controller> hanlderMap = new ConcurrentHashMap<>();
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();

    static {
        hanlderMap.put(URI.create("/index.html"), new IndexController());
    }

    public static Controller getController(HttpRequest request) {
        Controller controller = hanlderMap.get(request.getRequestUri());

        if (controller != null) {
            return controller;
        }

        return NOT_FOUND_CONTROLLER;
    }
}
