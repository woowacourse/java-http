package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.controller.StaticResourceController;

public class RequestMapping {

    private final Controller staticResourceController = new StaticResourceController();
    private final Map<String, Controller> controllers = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
