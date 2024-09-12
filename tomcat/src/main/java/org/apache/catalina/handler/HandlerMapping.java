package org.apache.catalina.handler;

import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.LogInController;
import org.apache.catalina.controller.RegisterAbstractController;
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
