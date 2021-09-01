package nextstep.joanne.handler;

import nextstep.joanne.handler.controller.Controller;
import nextstep.joanne.handler.controller.DefaultController;
import nextstep.joanne.http.request.HttpRequest2;

import java.util.Map;

public class HandlerMapping {
    private final Map<String, Controller> handlers;

    public HandlerMapping(Map<String, Controller> handlers) {
        this.handlers = handlers;
    }

    public Controller get(HttpRequest2 httpRequest) {
        return handlers.getOrDefault(httpRequest.requestLine().uri(), new DefaultController());
    }
}
