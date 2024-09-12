package org.apache.coyote.handler;

import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.LogInController;
import org.apache.coyote.controller.RegisterAbstractController;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequestHeader;

public class HandlerMapping {

    private static final Map<RequestMapping, AbstractController> HANDLER_MAPPER = Map.of(
            new RequestMapping(HttpMethod.POST, "/login"), new LogInController(),
            new RequestMapping(HttpMethod.POST, "/register"), new RegisterAbstractController()
    );

    public AbstractController getController(HttpRequestHeader request) {
        return HANDLER_MAPPER.get(new RequestMapping(request.getHttpMethod(), request.getPath()));
    }
}
