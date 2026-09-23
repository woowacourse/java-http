package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        if (request.getPath().startsWith("/login")) {
            return new LoginController();
        }
        if (request.getPath().startsWith("/register")) {
            return new RegisterController();
        }
        return new StaticResourceController();
    }
}
