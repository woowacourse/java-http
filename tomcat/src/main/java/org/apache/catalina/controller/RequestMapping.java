package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController = new StaticResourceController();

    public RequestMapping(SessionManager sessionManager) {
        controllers = Map.of(
                "/login", new LoginController(sessionManager),
                "/register", new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        String path = request.requestLine().path();
        return controllers.getOrDefault(path, staticResourceController);
    }
}
