package org.apache.catalina.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class HandlerMapping {

    private static final Map<String, Controller> handlers = new HashMap<>();

    static {
        handlers.put("/login", new LoginController());
        handlers.put("/index", new DefaultController());
        handlers.put("/register", new RegisterController());
    }

    /**
     * if a Controller is not found, return null
     * @param request HttpRequest
     * @return Controller
     */
    public Controller getController(final HttpRequest request) {
        return handlers.get(request.getPath());
    }
}
