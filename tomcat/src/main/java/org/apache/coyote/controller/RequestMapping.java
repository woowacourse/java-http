package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private RequestMapping() {
    }

    public static Controller getController(HttpRequest request) {
        if (request.getPath().startsWith("/login")) {
            return new LoginController();
        } else if (request.getPath().startsWith("/register")) {
            return new RegisterController();
        } else {
            return new PageController();
        }
    }
}
