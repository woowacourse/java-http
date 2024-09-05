package org.apache.coyote.handler;

import java.util.Map;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.LogInController;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.HttpRequest;

public class HandlerMapping {

    private static final Map<RequestMapping, Controller> HANDLER_MAPPER = Map.of(
            new RequestMapping("GET", "/login"), new LogInController()
    );

    public Controller getController(HttpRequest request) {
        return HANDLER_MAPPER.get(
                new RequestMapping(request.getHttpMethod(), request.getPath()));
    }
}
