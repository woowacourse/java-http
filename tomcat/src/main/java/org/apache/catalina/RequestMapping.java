package org.apache.catalina;

import org.apache.catalina.controller.HelloWorldController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.LogoutController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.SessionController;
import org.apache.catalina.resource.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public final class RequestMapping {

    private final Map<String, Controller> controllers = Map.of(
            "/", new HelloWorldController(),
            "/register", new RegisterController(),
            "/login", new LoginController(),
            "/session", new SessionController(),
            "/logout", new LogoutController()
    );
    private final Controller staticResourceController = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(
                request.getRequestUri().getPath(),
                staticResourceController
        );
    }
}
