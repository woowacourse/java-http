package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        if (request.getPath().equals("/login")) {
            return new LoginController();
        }
        if (request.getPath().equals("/register")) {
            return new RegisterController();
        }
        return null;
    }
}
