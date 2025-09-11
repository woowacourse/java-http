package org.apache.catalina;

import java.util.Map;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RootController;
import org.apache.catalina.controller.StaticController;
import org.apache.coyote.http11.Request;

public class RequestMapping {

    private static final Controller DEFAULT_CONTROLLER = new StaticController();
    private static final Map<String, Controller> REQUEST_MAPPING = Map.ofEntries(
            Map.entry("/", new RootController()),
            Map.entry("/login", new LoginController()),
            Map.entry("/register", new RegisterController())
    );

    public Controller getController(final Request request) {
        return REQUEST_MAPPING.getOrDefault(request.getRequestURI(), DEFAULT_CONTROLLER);
    }
}
