package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.interceptor.HandlerInterceptor;
import java.util.List;
import java.util.Map;

public class HandlerMapping {

    private final Map<String, Controller> controllers;
    private final List<HandlerInterceptor> handlerInterceptors;

    public HandlerMapping(final Map<String, Controller> controllers, final List<HandlerInterceptor> handlerInterceptors) {
        this.controllers = controllers;
        this.handlerInterceptors = handlerInterceptors;
    }

    public HandlerExecutionChain findController(final String path) {
        return new HandlerExecutionChain(controllers.get(path), handlerInterceptors);
    }
}
