package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.config.UnauthorizedInterceptor;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private final UnauthorizedInterceptor unauthorizedInterceptor;

    public RequestMapping() {
        this.unauthorizedInterceptor = new UnauthorizedInterceptor();
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(String path) {
        if (unauthorizedInterceptor.checkPath(path)) {
            return new UnauthorizedController();
        }

        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }
        if (getClass().getClassLoader().getResource("static" + path) == null) {
            return new NotFoundController();
        }
        return new DefaultController();
    }
}
