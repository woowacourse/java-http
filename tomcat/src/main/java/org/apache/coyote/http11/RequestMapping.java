package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Map;
import org.apache.catalina.Session;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller fallbackController;

    public RequestMapping(Map<String, Controller> controllers) {
        this(controllers, null);
    }

    public RequestMapping(Map<String, Controller> controllers, Controller fallbackController) {
        this.controllers = Map.copyOf(controllers);
        this.fallbackController = fallbackController;
    }

    public static RequestMapping forSession(Session session) {
        return new RequestMapping(
                Map.of("/", new RootController(),
                        "/login", new LoginController(session),
                        "/register", new RegisterController()),
                new StaticResourceController());
    }

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.requestLine().path(), fallbackController);
    }
}
