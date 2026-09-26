package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RequestMapping {

    private final Map<Route, Controller> controllers;
    private final Controller staticResourceController = new StaticResourceController();
    private final Controller methodNotAllowedController = request -> HttpResponse.empty(405, "Method Not Allowed");

    public RequestMapping() {
        Controller loginController = new LoginController();
        controllers = Map.of(
                new Route("GET", "/login"), loginController,
                new Route("POST", "/login"), loginController,
                new Route("POST", "/register"), new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        String method = request.requestLine().method();
        String path = request.requestLine().path();
        Controller controller = controllers.get(new Route(method, path));

        if (controller != null) {
            return controller;
        }

        if ("GET".equals(method)) {
            return staticResourceController;
        }

        return methodNotAllowedController;
    }

    private record Route(String method, String path) {

    }
}
