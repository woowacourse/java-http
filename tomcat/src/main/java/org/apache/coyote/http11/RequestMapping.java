package org.apache.coyote.http11;

import java.util.Map;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
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
