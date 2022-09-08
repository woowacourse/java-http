package org.apache.catalina.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;

import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class HandlerMapping {

    private static final HandlerMapping INSTANCE = new HandlerMapping();
    private static final Map<String, Controller> handlers = new HashMap<>();

    static {
        handlers.put("/login", new LoginController());
        handlers.put("/index", new DefaultController());
        handlers.put("/register", new RegisterController());
    }

    public static HandlerMapping getInstance() {
        return INSTANCE;
    }

    /**
     * if a Controller is not found, return null
     * @param request HttpRequest
     * @return Controller
     */
    public Controller getController(final HttpRequest request) {
        return handlers.get(request.getPath());
    }

    private HandlerMapping() {}
}
