package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerMapper {

    private static final Logger log = LoggerFactory.getLogger(ControllerMapper.class);
    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final Controller staticResourceController = new StaticResourceController();

    static {
        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        log.debug("request : {}", request);
        if (controllers.containsKey(request.getPath())) {
            return controllers.get(request.getPath());
        }
        return staticResourceController;
    }
}
