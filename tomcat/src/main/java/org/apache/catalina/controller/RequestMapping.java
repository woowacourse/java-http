package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new RootController());
        controllers.put("/index.html", new DefaultController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        final String path = request.getUri().getPath();

        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        return new StaticResourceController();
    }
}
