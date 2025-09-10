package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final StaticResourceController staticResourceController;

    public RequestMapping(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put("/", new DefaultController(staticResourceController));
        controllers.put("/register", new RegisterController(staticResourceController));
        controllers.put("/login", new LoginController(staticResourceController));
    }

    public Controller getController(final HttpRequest request) {
        final String path = request.requestLine().path().uri();
        return controllers.getOrDefault(path, staticResourceController);
    }
}
