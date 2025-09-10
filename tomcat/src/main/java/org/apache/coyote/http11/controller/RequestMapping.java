package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public Controller getController(final HttpRequest request) {
        if (request.isPathEqualsTo("/")) {
            return new HomeController();
        }
        if (request.isPathEqualsTo("/register")) {
            return new RegisterController();
        }
        if (request.isPathEqualsTo("/login")) {
            return new LoginController();
        }
        return new StaticRequestController();
    }

    private RequestMapping() {
    }
}
