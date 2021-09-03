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
        for (Map.Entry<String, Controller> entry : handlers.entrySet()) {
            if (httpRequest.uri().startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return new ResourceController();
    }
}
