package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http.request.HttpRequest;

public class ControllerRegistry {

    private static final Map<String, Controller> mappings = new ConcurrentHashMap<>();
    private static final StaticResourceController defaultController = new StaticResourceController();

    static {
        mappings.put("/login", new LoginController());
        mappings.put("/register", new RegisterController());
        mappings.put("/", new RootController());
    }

    private ControllerRegistry() {
    }

    public static Controller getController(final HttpRequest request) {
        String requestURI = request.getRequestURI();
        return mappings.getOrDefault(requestURI, defaultController);
    }
}
