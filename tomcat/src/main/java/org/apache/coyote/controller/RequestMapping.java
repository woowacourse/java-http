package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        if (request.getPath().equals("/login")) {
            new LoginController();
        }

        if (request.getPath().equals("/register")) {
            new RegisterController();
        }

        return new ResourceController();
    }

}
