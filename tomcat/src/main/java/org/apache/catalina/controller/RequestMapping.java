package org.apache.catalina.controller;

import java.util.Map;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.session.SessionIdGenerator;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;

    public RequestMapping(SessionManager sessionManager, SessionIdGenerator sessionIdGenerator) {
        this.controllers = Map.of(
                "/", new RootController(),
                "/login", new LoginController(sessionManager, sessionIdGenerator),
                "/register", new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.startLine().path(), new DefaultController());
    }
}
