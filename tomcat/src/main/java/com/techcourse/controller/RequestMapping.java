package com.techcourse.controller;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpRequest;

public class RequestMapping {

    private final SessionManager sessionManager;

    public RequestMapping(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Controller getController(HttpRequest httpRequest) {
        final String path = httpRequest.getTarget();

        if ("/".equals(path)) {
            return new RootController();
        } else if ("/login".equals(path)) {
            return new LoginController(sessionManager);
        } else if ("/register".equals(path)) {
            return new RegisterController();
        } else {
            return new StaticController();
        }
    }
}
