package nextstep.joanne.server.handler;

import nextstep.joanne.server.handler.controller.Controller;
import nextstep.joanne.server.handler.controller.ResourceController;
import nextstep.joanne.server.http.request.HttpRequest;

import java.util.Map;

public class HandlerMapping {
    private final Map<String, Controller> handlers;

    public HandlerMapping(Map<String, Controller> handlers) {
        this.handlers = handlers;
    }

    public Controller get(HttpRequest httpRequest) {
        return handlers.getOrDefault(httpRequest.requestLine().uri(), new ResourceController());
    }
}
