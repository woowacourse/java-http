package org.apache.catalina;

import org.apache.catalina.controller.HelloWorldController;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public final class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/", new HelloWorldController()
    );

    public Controller getController(HttpRequest request) {
        return controllers.get(request.getRequestUri().getPath());
    }
}
