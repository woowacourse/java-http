package nextstep.jwp.core;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.web.HttpRequest;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMap {
    private static final Map<URI, Controller> hanlderMap = new ConcurrentHashMap<>();

    static {
        hanlderMap.put(URI.create("/index.html"), new IndexController());
    }

    public static Controller getController(HttpRequest request) {
        return hanlderMap.get(request.getRequestUri());
    }
}
